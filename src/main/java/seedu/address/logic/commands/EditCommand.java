package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASSTIMING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.ClassTiming;
import seedu.address.model.person.Email;
import seedu.address.model.person.Location;
import seedu.address.model.person.Name;
import seedu.address.model.person.Nok;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Rate;
import seedu.address.model.person.Student;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_RATE + "RATE] "
            + "[" + PREFIX_CLASSTIMING + "CLASS TIMING] "
            + "[" + PREFIX_LOCATION + "LOCATION] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Student> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Student studentToEdit = lastShownList.get(index.getZeroBased());
        Student editedStudent = createEditedPerson(studentToEdit, editPersonDescriptor);

        if (!studentToEdit.isSamePerson(editedStudent) && model.hasPerson(editedStudent)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(studentToEdit, editedStudent);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedStudent));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Student createEditedPerson(Student studentToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert studentToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(studentToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(studentToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(studentToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(studentToEdit.getAddress());
        Rate updatedRate = editPersonDescriptor.getRate().orElse(studentToEdit.getRate());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(studentToEdit.getTags());
        ClassTiming classTiming = editPersonDescriptor.getClassTiming().orElse(studentToEdit.getClassTiming());
        Location updatedLocation = editPersonDescriptor.getLocation().orElse(studentToEdit.getLocation());

        // Nok
        Name nokName = editPersonDescriptor.getNokName().orElse(studentToEdit.getNok().getName());
        Phone nokPhone = editPersonDescriptor.getNokPhone().orElse(studentToEdit.getNok().getPhone());
        Email nokEmail = editPersonDescriptor.getNokEmail().orElse(studentToEdit.getNok().getEmail());
        Address nokAddress = editPersonDescriptor.getNokAddress().orElse(studentToEdit.getNok().getAddress());
        Nok nok = new Nok(nokName, nokPhone, nokEmail, nokAddress);

        return new Student(updatedName, updatedPhone, updatedEmail, updatedAddress,
                updatedRate, classTiming, updatedLocation, nok, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editPersonDescriptor.equals(e.editPersonDescriptor);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Rate rate;
        private Set<Tag> tags;
        private ClassTiming classTiming;
        private Location location;

        private Name nokName;
        private Phone nokPhone;
        private Email nokEmail;
        private Address nokAddress;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setRate(toCopy.rate);
            setClassTiming(toCopy.classTiming);
            setLocation(toCopy.location);

            setNokName(toCopy.nokName);
            setNokPhone(toCopy.nokPhone);
            setNokEmail(toCopy.nokEmail);
            setNokAddress(toCopy.nokAddress);

            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, rate, classTiming, location, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<ClassTiming> getClassTiming() {
            return Optional.ofNullable(classTiming);
        }

        public void setClassTiming(ClassTiming classTiming) {
            this.classTiming = classTiming;
        }

        public Optional<Location> getLocation() {
            return Optional.ofNullable(location);
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setRate(Rate rate) {
            this.rate = rate;
        }

        public Optional<Rate> getRate() {
            return Optional.ofNullable(rate);
        }

        // TODO: Find some way to reduce duplicate code fragments

        public void setNokName(Name nokName) {
            this.nokName = nokName;
        }

        public Optional<Name> getNokName() {
            return Optional.ofNullable(nokName);
        }
        public void setNokPhone(Phone nokPhone) {
            this.nokPhone = nokPhone;
        }

        public Optional<Phone> getNokPhone() {
            return Optional.ofNullable(nokPhone);
        }

        public void setNokEmail(Email nokEmail) {
            this.nokEmail = nokEmail;
        }

        public Optional<Email> getNokEmail() {
            return Optional.ofNullable(nokEmail);
        }

        public void setNokAddress(Address nokAddress) {
            this.nokAddress = nokAddress;
        }

        public Optional<Address> getNokAddress() {
            return Optional.ofNullable(nokAddress);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            // state check
            EditPersonDescriptor e = (EditPersonDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getAddress().equals(e.getAddress())
                    && getRate().equals(e.getRate())
                    && getClassTiming().equals(e.getClassTiming())
                    && getTags().equals(e.getTags());
        }
    }
}
