package ch.wisv.events.core.service.googlewallet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.wisv.events.core.exception.normal.TicketPassFailedException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.services.walletobjects.model.*;
import com.google.auth.oauth2.ServiceAccountCredentials;

import jakarta.validation.constraints.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class GoogleWalletServiceImpl implements GoogleWalletService {
    /** Service account credentials for Google Wallet APIs. */
    public static ServiceAccountCredentials credentials;

    @Value("${googleWallet.serviceKeyPath}")
    @NotNull
    private String serviceKeyPath;

    @Value("${googleWallet.issuerId}")
    @NotNull
    private String issuerId;

    @Value("${googleWallet.baseUrl}")
    @NotNull
    private String baseUrl;

    @Value("${googleWallet.origin}")
    @NotNull
    private String origin;

    @Value("${links.gtc}")
    @NotNull
    private String linkGTC;

    /**
     * Get Google Wallet pass for a Ticket.
     * 
     * @param ticket of type Ticket.
     * @return A link the user can use to add the ticket to their wallet.
     * @throws TicketPassFailedException when pass is not generated
     */
    public String getPass(Ticket ticket) throws TicketPassFailedException {
        if (credentials == null) {
            try {
                credentials = ServiceAccountCredentials.fromStream(new FileInputStream(this.serviceKeyPath));
            } catch (IOException e) {
                System.out.println("WARN: Failed to parse service account");
                System.out.println(e);
                return "https://pay.google.com/gp/v/save/FAILED";
            }
        }

        Product product = ticket.getProduct();
        EventTicketClass ticketClass = this.createClass(product);
        EventTicketObject ticketObject = this.createObject(ticket);

        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("iss", credentials.getClientEmail());
        claims.put("aud", "google");
        claims.put("origins", List.of(origin));
        claims.put("typ", "savetowallet");
        claims.put("iat", Instant.now().getEpochSecond());

        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("eventTicketClasses", List.of(ticketClass));
        payload.put("eventTicketObjects", List.of(ticketObject));
        claims.put("payload", payload);

        Algorithm algorithm = Algorithm.RSA256(
                null, (RSAPrivateKey) credentials.getPrivateKey());
        String token = JWT.create().withPayload(claims).sign(algorithm);

        return String.format("https://pay.google.com/gp/v/save/%s", token);
    }

    /**
     * Create the passes class based on the product.
     * 
     * @param product The product to base it on.
     * @return A Google compatible Ticket class.
     */
    private EventTicketClass createClass(Product product) {
        String homePage = product.getEvent().hasExternalProductUrl()
                ? product.getEvent().getExternalProductUrl()
                : baseUrl;
        Uri tnc = new Uri()
                .setUri(linkGTC)
                .setDescription("Terms & Conditions")
                .setId("LINK_GTC");

        String locationName = product.getEvent().getLocation();

        if(locationName == null || locationName.trim().isEmpty()) {
            locationName = "Unknown";
        }

        return new EventTicketClass()
                .setId(this.getClassId(product))
                .setIssuerName("Christiaan Huygens")
                .setReviewStatus("UNDER_REVIEW")
                .setHexBackgroundColor("#1e274a")
                .setEventName(this.makeLocalString(this.formatProduct(product)))
                .setWideLogo(this.makeImage(String.format("%s/images/ch-logo.png", baseUrl)))
                .setLogo(this.makeImage(String.format("%s/icons/apple-touch-icon.png", baseUrl)))
                .setLinksModuleData(new LinksModuleData().setUris(Arrays.asList(tnc)))
                .setHomepageUri(new Uri()
                        .setUri(homePage)
                        .setDescription("Events"))
                .setVenue(new EventVenue()
                        .setName(this.makeLocalString(locationName))
                        .setAddress(this.makeLocalString("Mekelweg 4, 2628 CD Delft")))
                .setDateTime(new EventDateTime()
                        .setStart(this.formatDate(product.getEvent().getStart()))
                        .setEnd(this.formatDate(product.getEvent().getEnding())));
    }

    private EventTicketObject createObject(Ticket ticket) {
        Money cost = new Money().setCurrencyCode("EUR").setMicros((long) (ticket.getProduct().cost * 1000000));
        Uri tnc = new Uri()
                .setUri(linkGTC)
                .setDescription("Terms & Conditions")
                .setId("LINK_GTC");

        return new EventTicketObject()
                .setId(this.getObjectId(ticket))
                .setClassId(this.getClassId(ticket.getProduct()))
                .setState("ACTIVE")
                .setTicketNumber(ticket.getKey())
                .setTicketHolderName(ticket.getOwner().getName())
                .setHexBackgroundColor("#1e274a")
                .setFaceValue(cost)
                .setBarcode(new Barcode().setType("QR_CODE").setValue(ticket.getUniqueCode()))
                .setLinksModuleData(new LinksModuleData().setUris(Arrays.asList(tnc)));
    }

    /**
     * Get the ID of a product.
     * 
     * @param product The product to derive the ID.
     * @return The ID
     */
    private String getClassId(Product product) {
        return String.format("%s.%s", issuerId, product.getKey());
    }

    /**
     * Get the object ID of a ticket.
     * 
     * @param ticket The ticket to get the ID for.
     * @return The ID
     */
    private String getObjectId(Ticket ticket) {
        return String.format("%s-%s", this.getClassId(ticket.getProduct()), ticket.getKey());
    }

    /**
     * Format the product name accorting to if they have a second product
     * 
     * @param product
     * @return
     */
    private String formatProduct(Product product) {
        Event event = product.getEvent();

        if (event.getProducts().size() <= 1) {
            return event.getTitle();
        } else {
            return String.format("%s - %s", event.getTitle(), product.getTitle());
        }
    }

    /**
     * Format a local date to the string format Google expects.
     * 
     * @param localDate The Java date object.
     * @return The date in string form.
     */
    private String formatDate(LocalDateTime localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSX");
        String formattedDate = localDate.atOffset(ZoneOffset.UTC).format(formatter);
        return formattedDate;
    }

    private LocalizedString makeLocalString(String str) {
        TranslatedString defaultLang = new TranslatedString()
                .setLanguage("en-US")
                .setValue(str);
        return new LocalizedString().setDefaultValue(defaultLang);
    }

    private Image makeImage(String src) {
        ImageUri uri = new ImageUri().setUri(src);
        return new Image().setSourceUri(uri);
    }
}
