package ch.wisv.events.utils;

public enum LdapGroup {

    AKCIE("AkCie"),
    ANNUCIE("AnnuCie"),
    BT("Business Tour"),
    BEHEER("Beheer"),
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
    CC("CC"),
    HACKCIE("HackCie"),
    CHESSCO("CHessCo"),
    COH("CoH"),
    EIWEIW("EIWEIW"),
    PUZZLEPIECIE("PuzzlePieCie");

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

    /**
     * Method intToString returns the name of the i-th LdapGroup.
     *
     * @param i of type int
     * 
     * @return String
     */
    public static String intToString(int i) {
        return LdapGroup.values()[i].getName();
    }

}
