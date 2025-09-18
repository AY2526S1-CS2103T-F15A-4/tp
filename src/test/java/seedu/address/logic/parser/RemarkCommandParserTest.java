package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import org.junit.jupiter.api.Test;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.RemarkCommandParser;
import seedu.address.model.person.Remark;

class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    void parse_validArgs_returnsRemarkCommand() throws Exception {
        var expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("hello"));
        var cmd = parser.parse("1 r/hello");
        assertEquals(expectedCommand, cmd);
    }

    @Test
    void parse_emptyRemark_returnsRemarkCommandWithEmpty() throws Exception {
        var expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        var cmd = parser.parse("1 r/");
        assertEquals(expectedCommand, cmd);
    }
}
