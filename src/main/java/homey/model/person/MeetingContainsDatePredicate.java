package homey.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.function.Predicate;

import homey.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Meeting} matches the date given.
 */
public class MeetingContainsDatePredicate implements Predicate<Person> {
    private final LocalDate date;

    /**
     * Constructs a MeetingContainsDatePredicate with the given date.
     */
    public MeetingContainsDatePredicate(String date) {
        requireNonNull(date);
        this.date = LocalDate.parse(date);
    }

    @Override
    public boolean test(Person person) {
        return person.getMeeting()
                .map(Meeting::getDateTime)
                .map(dateTime -> dateTime.toLocalDate().equals(this.date))
                .orElse(false);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof MeetingContainsDatePredicate)
                && this.date.equals(((MeetingContainsDatePredicate) other).date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("date", date).toString();
    }
}
