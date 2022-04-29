package ch.wisv.events.utils;

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
    HACKCIE("HackCie"),
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

    /** Nam of the LdapGroup. */
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
     * Method getName returns the name of this LdapGroup object.
     *
     * @return the name (type String) of this LdapGroup object.
     */
    public String getName() {
        return name;
    }

}
