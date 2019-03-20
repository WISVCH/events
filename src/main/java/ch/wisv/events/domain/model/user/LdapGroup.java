package ch.wisv.events.domain.model.user;

import java.util.Arrays;
import lombok.Getter;

/**
 * LdapGroup enum containing a list of all active committees of CH.
 */
@SuppressWarnings("CheckStyle")
public enum LdapGroup {

    AKCIE("AkCie"),
    ANNUCIE("AnnuCie"),
    BT("Business Tour"),
    CHBEHEER("CH Beheer"),
    CHIPCIE("CHipCie"),
    CHOCO("CHoCo"),
    COMMA("ComMA"),
    DIES("Dies"),
    DIENST2("Dienst2"),
    FACIE("FaCie"),
    FILMCREW("Filmcrew"),
    FLITCIE("FlitCie"),
    GALACIE("GalaCie"),
    ICOM("iCom"),
    LANCIE("LANcie"),
    LUCIE("LuCie"),
    MACHAZINE("MaCHazine"),
    MAPHYA("MaPhyA"),
    MATCH("MatCH"),
    MEISCIE("MeisCie"),
    REISCOMMISSIE("Reiscommissie"),
    SJAARCIE("SjaarCie"),
    SYMPOSIUM("Symposiumcommissie"),
    VERDIEPCIE("VerdiepCie"),
    W3CIE("W3Cie"),
    WHISCO("WhisCo"),
    WIFI("WiFi"),
    BESTUUR("Bestuur"),
    VOORZITTER("Voorzitter"),
    SECRETARIS("Secretaris"),
    PENNINGMEESTER("Penningmeester"),
    COW("COW"),
    COI("COI"),
    CPR("CPR"),
    CC("CC");

    /**
     * Name of the LdapGroup.
     */
    @Getter
    private final String name;

    /**
     * Constructor LdapGroup creates a new LdapGroup instance.
     *
     * @param name of type String
     */
    LdapGroup(String name) {
        this.name = name;
    }

    /**
     * Get an LdapGroup by name
     *
     * @param name of type String
     *
     * @return LdapGroup
     */
    public static LdapGroup getByName(String name) {
        return Arrays.stream(LdapGroup.values())
                .filter(ldapGroup -> ldapGroup.getDeclaringClass().toGenericString().equals(name))
                .findFirst()
                .orElse(null);
    }
}
