package ch.wisv.events.core.admin;


public interface Attendence {
    long getTicketsCount();
    long getScannedCount();
    double getPercentageScanned();
}
