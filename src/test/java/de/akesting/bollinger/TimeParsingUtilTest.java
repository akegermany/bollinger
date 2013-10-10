package de.akesting.bollinger;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

public class TimeParsingUtilTest {

    @Test
    public void test() {
        // fail("Not yet implemented");
        // DateTime dateTime = timeFormatter.parseDateTime(record.get(xColum)).toDateTime(DateTimeZone.UTC);
        DateTime dateTime = LocalDateTime.parse("Mar 25, 2013 5:32:08 PM +0200",
                DateTimeFormat.forPattern("MMM d, YYYY h:mm:ss a Z")).toDateTime(DateTimeZone.UTC);
        long time = TimeUnit.MILLISECONDS.toSeconds(dateTime.getMillis());
        System.out.println("dateTime=" + dateTime + " --> seconds=" + time);

        dateTime = LocalDateTime.parse("2013-05-01T05:32:08 PM", DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ss a"))
                .toDateTime(DateTimeZone.UTC);
        time = TimeUnit.MILLISECONDS.toSeconds(dateTime.getMillis());
        System.out.println("dateTime=" + dateTime + " --> seconds=" + time);
    }

}
