package ch.wisv.events.admin.utils;

import ch.wisv.events.core.model.event.EventCategory;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * EventsTemplate enum.
 */
public enum EventTemplate {

    TUESDAY_LECTURE(
            "T.U.E.S.Day Lecture",
            "T.U.E.S.Day Lecture: ",
            "T.U.E.S.Day Lecture: This lecture will be about ",
            "T.U.E.S.Day Lecture: This lecture will be about ",
            LocalDateTime.now().withHour(12).withMinute(45),
            LocalDateTime.now().withHour(13).withMinute(30),
            "Lecture Hall ",
            50,
            150,
            ImmutableList.of(EventCategory.EDUCATIONAL)
    ),

    TUESDAY_LECTURE_WITH_DRINKS(
            "T.U.E.S.Day Lecture with Drinks",
            "T.U.E.S.Day Lecture with Drinks: ",
            "",
            "",
            LocalDateTime.now().withHour(16).withMinute(0),
            LocalDateTime.now().withHour(18).withMinute(0),
            "Lecture Hall ... + /Pub",
            50,
            150,
            ImmutableList.of(EventCategory.EDUCATIONAL, EventCategory.SOCIAL)
    );

    /** Name of the template. */
    @Getter
    private final String templateName;

    /** Event title. */
    @Getter
    private final String title;

    /** Event shortDescription. */
    @Getter
    private final String shortDescription;

    /** Event description. */
    @Getter
    private final String description;

    /** Event startingTime. */
    @Getter
    private final LocalDateTime startingTime;

    /** Event endingTime. */
    @Getter
    private final LocalDateTime endingTime;

    /** Event location. */
    @Getter
    private final String location;

    /** Event target. */
    @Getter
    private final int target;

    /** Event maxSold. */
    @Getter
    private final int maxSold;

    /** List of EventCategories. */
    @Getter
    private final List<EventCategory> categories;

    /**
     * Events template.
     *
     * @param templateName     of type String
     * @param title            of type String
     * @param shortDescription of type String
     * @param description      of type String
     * @param startingTime     of type LocalDateTime
     * @param endingTime       of type LocalDateTime
     * @param location         of type String
     * @param target           of type int
     * @param maxSold          of type int
     * @param categories       of type EventCategory
     */
    EventTemplate(
            String templateName, String title,
            String shortDescription,
            String description,
            LocalDateTime startingTime,
            LocalDateTime endingTime,
            String location,
            int target,
            int maxSold,
            List<EventCategory> categories
    ) {
        this.templateName = templateName;
        this.title = title;
        this.shortDescription = shortDescription;
        this.description = description;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.location = location;
        this.target = target;
        this.maxSold = maxSold;
        this.categories = categories;
    }

    /**
     * Convert template to JSON String.
     *
     * @return String
     */
    public String toJson() {
        JSONObject object = new JSONObject();

        object.put("title", this.getTitle());
        object.put("shortDescription", this.getShortDescription());
        object.put("description", this.getDescription());
        object.put("startingTime", this.getStartingTime().toString());
        object.put("endingTime", this.getEndingTime().toString());
        object.put("location", this.getLocation());
        object.put("target", this.getTarget());
        object.put("maxSold", this.getMaxSold());

        JSONArray categories = new JSONArray();
        categories.addAll(this.getCategories());
        object.put("categories", categories);

        return object.toString();
    }
}
