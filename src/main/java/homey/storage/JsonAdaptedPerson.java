package homey.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import homey.commons.exceptions.IllegalValueException;
import homey.model.person.Address;
import homey.model.person.Email;
import homey.model.person.Meeting;
import homey.model.person.Name;
import homey.model.person.Person;
import homey.model.person.Phone;
import homey.model.person.Remark;
import homey.model.tag.Relation;
import homey.model.tag.Tag;
import homey.model.tag.TransactionStage;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String relation;
    private final String stage;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final String meeting;
    private final String remark;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("relation") String relation, @JsonProperty("stage") String stage,
            @JsonProperty("remark") String remark, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("meeting") String meeting) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.relation = relation;
        this.stage = stage;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.meeting = meeting;
        this.remark = remark;
    }

    /**
     * Overloaded constructor
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
                             String relation, String stage, String remark, List<JsonAdaptedTag> tags) {
        this(name, phone, email, address, relation, stage, remark, tags, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        stage = source.getStage().value;
        relation = source.getRelation().value;
        remark = source.getRemark().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        this.meeting = source.getMeeting().map(Meeting::toString).orElse(null);
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (relation == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Relation.class.getSimpleName()));
        }
        final Relation modelRelation = new Relation(relation);

        if (stage == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    TransactionStage.class.getSimpleName()));
        }
        if (!TransactionStage.isValid(stage)) {
            throw new IllegalValueException(TransactionStage.MESSAGE_CONSTRAINTS);
        }
        final TransactionStage modelStage = new TransactionStage(stage);

        Optional<Meeting> modelMeeting = Optional.empty();
        if (meeting != null && !meeting.trim().isEmpty()) {
            if (!Meeting.isValidMeeting(meeting)) {
                throw new IllegalValueException(Meeting.MESSAGE_CONSTRAINTS);
            }
            modelMeeting = Optional.of(new Meeting(meeting));
        }

        if (remark == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Remark.class.getSimpleName()));
        }
        final Remark modelRemark = new Remark(remark);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelRelation, modelStage, modelRemark,
                modelTags, modelMeeting);
    }

}
