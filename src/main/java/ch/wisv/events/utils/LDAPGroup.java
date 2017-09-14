package ch.wisv.events.utils;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public enum LDAPGroup {

    AKCIE("Akcie"),
    ANNUCIE("Annucie"),
    BT("Business Tour"),
    BEHEER("CH Beheer"),
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
     * Field name
     */
    private final String name;

    /**
     * Constructor LDAPGroup creates a new LDAPGroup instance.
     *
     * @param name of type String
     */
    LDAPGroup(String name) {
        this.name = name;
    }

    /**
     * Method getName returns the name of this LDAPGroup object.
     *
     * @return the name (type String) of this LDAPGroup object.
     */
    public String getName() {
        return name;
    }

}
