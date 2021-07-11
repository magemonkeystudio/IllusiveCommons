/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub.annotated;

import me.filoghost.fcommons.command.CommandContext;
import me.filoghost.fcommons.command.sub.SubCommand;
import me.filoghost.fcommons.command.sub.SubCommandContext;
import me.filoghost.fcommons.command.validation.CommandException;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AnnotatedSubCommandManagerTest {

    @Test
    void testDisplayOrder() {
        TestImplementation manager = new TestImplementation();

        assertThat(manager.getSubCommands()).extracting(SubCommand::getName).containsExactly(
                "z",
                "a",
                "B",
                "c",
                "D"
        );
    }

    @Test
    void testSubCommandCall() throws CommandException {
        TestImplementation manager = new TestImplementation();

        CommandContext contextZ = new CommandContext(null, null, new String[]{"z", "testArgZ"});
        manager.execute(contextZ.getSender(), contextZ.getArgs(), contextZ);

        CommandContext contextB = new CommandContext(null, null, new String[]{"b", "testArgB"});
        manager.execute(contextB.getSender(), contextB.getArgs(), contextB);

        assertThat(manager.zArg0).isEqualTo("testArgZ");
        assertThat(manager.bArg0).isEqualTo("testArgB");
    }

    @Test
    void testOverwriteThrowsException() {
        TestImplementation manager = new TestImplementation();

        AnnotatedSubCommand testOverrideCommand = new AnnotatedSubCommand() {

            {
                setName("Z"); // Use different case
                setDisplayPriority(99); // Use different priority
            }

            @Override
            public void execute(CommandSender sender, String[] args, SubCommandContext context) {}

        };

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
                () -> manager.registerSubCommand(testOverrideCommand));
    }


    private static class TestImplementation extends AnnotatedSubCommandManager {

        private String bArg0;
        private String zArg0;

        public TestImplementation() {
            registerSubCommand(new AnnotatedSubCommand() {

                {
                    setName("B");
                }

                @Override
                public void execute(CommandSender sender, String[] args, SubCommandContext context) {
                    bArg0 = args[0];
                }

            });
        }

        @Name("z")
        @DisplayPriority(1)
        public void z(String[] args) {
            zArg0 = args[0];
        }

        @Name("D")
        public void d() {}

        @Name("a")
        public void a(CommandSender sender) {}

        @Name("c")
        public void c(SubCommandContext context) {}

    }

}
