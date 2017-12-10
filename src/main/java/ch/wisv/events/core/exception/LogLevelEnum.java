package ch.wisv.events.core.exception;

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
//@Slf4j
public enum LogLevelEnum {

    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE;

    public void logMessage(String message) {
        //
    }

//    ERROR(log::error),
//    WARN(log::warn),
//    INFO(log::info),
//    DEBUG(log::debug),
//    TRACE(log::trace);
//
//    private final Consumer<String> logger;
//
//    LogLevelEnum(Consumer<String> logger) {
//        this.logger = logger;
//    }
//
//    public void logMessage(String message) {
//        logger.accept(message);
//    }
}
