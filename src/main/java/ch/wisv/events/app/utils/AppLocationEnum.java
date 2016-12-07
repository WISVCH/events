package ch.wisv.events.app.utils;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

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
public enum AppLocationEnum {

    DASHBOARD("Dashboard", "fa-dashboard", "/dashboard/", new SimpleGrantedAuthority("ROLE_ADMIN")),
    SALES("Sales app", "fa-ticket", "/sales/", new SimpleGrantedAuthority("ROLE_USER"));

    /**
     * Field name
     */
    @Getter
    private final String name;

    /**
     * Field icon
     */
    @Getter
    private final String icon;

    /**
     * Field url
     */
    @Getter
    private final String url;

    /**
     * Field authority
     */
    @Getter
    private final SimpleGrantedAuthority authority;

    /**
     * Constructor AppLocationEnum creates a new AppLocationEnum instance.
     *
     * @param name      of type String
     * @param icon      of type String
     * @param url       of type String
     * @param authority of type SimpleGrantedAuthority
     */
    AppLocationEnum(String name, String icon, String url, SimpleGrantedAuthority authority) {
        this.name = name;
        this.icon = icon;
        this.url = url;
        this.authority = authority;
    }

    /**
     * Method getLocations returns list of locations accessable.
     *
     * @param authority of type Collection<GrantedAuthority>
     * @return List<AppLocationEnum>
     */
    public static List<AppLocationEnum> getLocations(Collection<GrantedAuthority> authority) {
        List<AppLocationEnum> location = new ArrayList<>();
        stream(AppLocationEnum.values()).forEach(x -> authority.forEach(p -> {
            SimpleGrantedAuthority simple = (SimpleGrantedAuthority) p;
            if (simple.equals(x.getAuthority())) {
                location.add(x);
            }
        }));

        return location.stream().distinct().collect(Collectors.toList());
    }
}
