/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub;

import me.filoghost.fcommons.command.CommandException;
import me.filoghost.fcommons.command.annotation.DisplayPriority;
import me.filoghost.fcommons.command.annotation.Name;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AnnotatedSubCommandManagerTest {

    @Test
    void testSubCommandRegistrationAndOrder() {
        TestImplementation manager = new TestImplementation();

        assertThat(manager.getSubCommands()).extracting(SubCommand::getName).containsExactly(
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
        TestImplementation manager = new TestImplementation();
        manager.execute(null, "test", new String[]{"z", "arg"});
        manager.execute(null, "test", new String[]{"b"});

        assertThat(manager.zCalled).isTrue();
        assertThat(manager.bCalled).isTrue();
    }

    private static class TestImplementation extends AnnotatedSubCommandManager {

        private boolean zCalled;
        private boolean bCalled;

        public TestImplementation() {
            registerSubCommand(new SimpleSubCommand("b") {

                @Override
                public void execute(SubCommandExecution subCommandExecution) {
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
