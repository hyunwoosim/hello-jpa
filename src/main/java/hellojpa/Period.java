package hellojpa;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class Period {

    //기간
    private LocalDateTime StartDate;
    private LocalDateTime endDate;



    public LocalDateTime getStartDate() {
        return StartDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        StartDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
