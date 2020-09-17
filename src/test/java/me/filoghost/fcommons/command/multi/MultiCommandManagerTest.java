package me.filoghost.fcommons.command.multi;

import me.filoghost.fcommons.command.CommandException;
import me.filoghost.fcommons.command.annotation.DisplayPriority;
import me.filoghost.fcommons.command.annotation.Name;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MultiCommandManagerTest {

	@Test
	void testSubCommandRegistrationAndOrder() {
		MultiCommandTest multiCommandTest = new MultiCommandTest("test");

		assertThat(multiCommandTest.getAllSubCommands()).extracting(SubCommand::getName).containsExactly(
				"z",
				"a",
				"b",
				"C",
				"D",
				"e"
		);
	}

	@Test
	void testSubCommandCall() throws CommandException {
		MultiCommandTest multiCommandTest = new MultiCommandTest("test");
		multiCommandTest.execute(null, "test", new String[]{"z", "arg"});
		multiCommandTest.execute(null, "test", new String[]{"b"});

		assertThat(multiCommandTest.zCalled).isTrue();
		assertThat(multiCommandTest.bCalled).isTrue();
	}

	private static class MultiCommandTest extends MultiCommandManager {

		private boolean zCalled;
		private boolean bCalled;

		public MultiCommandTest(String label) {
			super(label);

			registerSubCommand(new SimpleSubCommand("b") {

				@Override
				public void execute(SubCommandSession subCommandSession) {
					bCalled = true;
				}

			});
		}

		@Name("z")
		@DisplayPriority(1)
		public void z(CommandSender sender, String[] args) {
			zCalled = true;
		}

		@Name("D")
		public void d(CommandSender sender, String[] args) {}

		@Name("a")
		public void a(CommandSender sender, String[] args) {}

		@Name("e")
		public void e(CommandSender sender, String[] args) {}

		@Name("C")
		public void c(CommandSender sender, String[] args) {}

	}

}