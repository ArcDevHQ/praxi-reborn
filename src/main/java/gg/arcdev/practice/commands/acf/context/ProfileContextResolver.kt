package gg.arcdev.practice.commands.acf.context

import co.aikar.commands.BukkitCommandCompletionContext
import co.aikar.commands.BukkitCommandExecutionContext
import co.aikar.commands.InvalidCommandArgument
import gg.arcdev.practice.core.profile.Profile
import gg.arcdev.practice.commands.acf.CommonCommandContext

class ProfileContextResolver : CommonCommandContext<Profile>(
    "profiles",
    Profile::class.java
) {
    override fun getContext(arg: BukkitCommandExecutionContext): Profile {
        val firstArg = arg.popFirstArg()
        return Profile.getByUsername(firstArg)
            ?: throw InvalidCommandArgument("Profile with that username does not exist!")
    }

    override fun getCompletions(context: BukkitCommandCompletionContext): Collection<String> {
        return Profile.getKnownUsernames()
    }
}
