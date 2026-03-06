package gg.arcdev.practice.game.arena.cuboid

/**
 * Represents directions that can be applied to certain faces and actions of a Cuboid
 */
enum class CuboidDirection {
    NORTH,
    EASY,
    SOUTH,
    WEST,
    UP,
    DOWN,
    HORIZONTAL,
    VERTICAL,
    BOTH,
    UNKNOWN;

    fun opposite(): CuboidDirection {
        return when (this) {
            NORTH -> SOUTH
            EASY -> WEST
            SOUTH -> NORTH
            WEST -> EASY
            HORIZONTAL -> VERTICAL
            VERTICAL -> HORIZONTAL
            UP -> DOWN
            DOWN -> UP
            BOTH -> BOTH
            else -> UNKNOWN
        }
    }
}