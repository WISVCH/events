package ch.wisv.events.scan.object;

import lombok.Getter;

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
public enum ScanResult {

    /**
     * Scan result when the SoldProduct is scanned the first time.
     */
    SUCCESSFUL("Scan successful", "#3d8b3d"),

    /**
     * Scan result when the SoldProduct is already scanned.
     */
    ALREADY_SCANNED("Ticket already scanned", "#df8a13"),

    /**
     * Scan result when the SoldProduct does not exists.
     */
    PRODUCT_NOT_EXISTS("There is no ticket available", "#b52b27"),

    /**
     * Scan result when there are multiple ticket on the same account
     */
    MULTIPLE_PRODUCT("", "#df8a13");

    /**
     * Field header
     */
    @Getter
    private final String header;

    /**
     * Field colour
     */
    @Getter
    private final String colour;

    /**
     * Constructor ScanResult creates a new ScanResult instance.
     *
     * @param header of type String
     * @param colour of type String
     */
    ScanResult(String header, String colour) {
        this.header = header;
        this.colour = colour;
    }
}
