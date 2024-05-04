package ch.wisv.events.core.admin;


public interface Attendance {
    long getTicketsCount();
    long getScannedCount();
    double getPercentageScanned();
}
