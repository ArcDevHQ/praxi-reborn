package gg.arcdev.practice.game.arena.cuboid

import lombok.Data
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Data
open class Cuboid(private val worldName: String, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) :
    Iterable<Location?> {
    /**
     * Get the minimum X coord of this Cuboid
     *
     * @return the minimum X coord
     */
    val lowerX: Int

    /**
     * Get the minimum Y coord of this Cuboid
     *
     * @return the minimum Y coord
     */
    val lowerY: Int

    /**
     * Get the minimum Z coord of this Cuboid
     *
     * @return the minimum Z coord
     */
    val lowerZ: Int

    /**
     * Get the maximum X coord of this Cuboid
     *
     * @return the maximum X coord
     */
    val upperX: Int

    /**
     * Get the maximum Y coord of this Cuboid
     *
     * @return the maximum Y coord
     */
    val upperY: Int

    /**
     * Get the maximum Z coord of this Cuboid
     *
     * @return the maximum Z coord
     */
    val upperZ: Int

    /**
     * Construct a Cuboid given two Location objects which represent any two corners of the Cuboid.
     *
     * @param l1 one of the corners
     * @param l2 the other corner
     */
    constructor(l1: Location, l2: Location) : this(
        l1.world.name,
        l1.blockX, l1.blockY, l1.blockZ,
        l2.blockX, l2.blockY, l2.blockZ
    )

    /**
     * Construct a Cuboid in the given World and xyz coords
     *
     * @param world the Cuboid's world
     * @param x1    X coord of corner 1
     * @param y1    Y coord of corner 1
     * @param z1    Z coord of corner 1
     * @param x2    X coord of corner 2
     * @param y2    Y coord of corner 2
     * @param z2    Z coord of corner 2
     */
    constructor(world: World, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) : this(
        world.name,
        x1,
        y1,
        z1,
        x2,
        y2,
        z2
    )

    /**
     * Construct a Cuboid in the given world name and xyz coords.
     *
     * @param worldName the Cuboid's world name
     * @param x1        X coord of corner 1
     * @param y1        Y coord of corner 1
     * @param z1        Z coord of corner 1
     * @param x2        X coord of corner 2
     * @param y2        Y coord of corner 2
     * @param z2        Z coord of corner 2
     */
    init {
        this.lowerX = min(x1, x2)
        this.upperX = max(x1, x2)
        this.lowerY = min(y1, y2)
        this.upperY = max(y1, y2)
        this.lowerZ = min(z1, z2)
        this.upperZ = max(z1, z2)
    }

    fun getX1(): Int = lowerX

    fun getY1(): Int = lowerY

    fun getZ1(): Int = lowerZ

    fun getX2(): Int = upperX

    fun getY2(): Int = upperY

    fun getZ2(): Int = upperZ

    val lowerCorner: Location
        /**
         * Get the Location of the lower northeast corner of the Cuboid (minimum XYZ coords).
         *
         * @return Location of the lower northeast corner
         */
        get() = Location(this.world, lowerX.toDouble(), lowerY.toDouble(), lowerZ.toDouble())

    val upperCorner: Location
        /**
         * Get the Location of the upper southwest corner of the Cuboid (maximum XYZ coords).
         *
         * @return Location of the upper southwest corner
         */
        get() = Location(this.world, upperX.toDouble(), upperY.toDouble(), upperZ.toDouble())

    val center: Location
        /**
         * Get the the center of the Cuboid
         *
         * @return Location at the centre of the Cuboid
         */
        get() = Location(
            this.world,
            this.lowerX + (this.upperX - this.lowerX).toDouble() / 2,
            this.lowerY + (this.upperY - this.lowerY).toDouble() / 2,
            this.lowerZ + (this.upperZ - this.lowerZ).toDouble() / 2
        )

    val world: World
        /**
         * Get the Cuboid's world.
         *
         * @return the World object representing this Cuboid's world
         *
         * @throws IllegalStateException if the world is not loaded
         */
        get() {
            val world = Bukkit.getWorld(worldName)
            checkNotNull(world) { "world '$worldName' is not loaded" }
            return world
        }

    val sizeX: Int
        /**
         * Get the size of this Cuboid along the X axis
         *
         * @return Size of Cuboid along the X axis
         */
        get() = (this.upperX - this.lowerX) + 1

    val sizeY: Int
        /**
         * Get the size of this Cuboid along the Y axis
         *
         * @return Size of Cuboid along the Y axis
         */
        get() = (this.upperY - this.lowerY) + 1

    val sizeZ: Int
        /**
         * Get the size of this Cuboid along the Z axis
         *
         * @return Size of Cuboid along the Z axis
         */
        get() = (this.upperZ - this.lowerZ) + 1

    val corners: Array<Location?>
        /**
         * Get the Blocks at the four corners of the Cuboid, without respect to y-value
         *
         * @return array of Block objects representing the Cuboid corners
         */
        get() {
            val res = arrayOfNulls<Location>(4)
            val w = this.world
            res[0] = Location(w, lowerX.toDouble(), 0.0, lowerZ.toDouble()) // ++x
            res[1] = Location(w, upperX.toDouble(), 0.0, lowerZ.toDouble()) // ++z
            res[2] = Location(w, upperX.toDouble(), 0.0, upperZ.toDouble()) // --x
            res[3] = Location(w, lowerX.toDouble(), 0.0, upperZ.toDouble()) // --z
            return res
        }

    /**
     * Expand the Cuboid in the given direction by the given amount. Negative amounts will shrink the Cuboid in the
     * given direction. Shrinking a cuboid's face past the opposite face is not an error and will return a valid
     * Cuboid.
     *
     * @param dir    the direction in which to expand
     * @param amount the number of blocks by which to expand
     *
     * @return a new Cuboid expanded by the given direction and amount
     */
    fun expand(dir: CuboidDirection, amount: Int): Cuboid {
        when (dir) {
            CuboidDirection.NORTH -> return Cuboid(
                worldName, this.lowerX - amount,
                this.lowerY,
                this.lowerZ,
                this.upperX,
                this.upperY,
                this.upperZ
            )

            CuboidDirection.SOUTH -> return Cuboid(
                worldName,
                this.lowerX,
                this.lowerY,
                this.lowerZ, this.upperX + amount,
                this.upperY,
                this.upperZ
            )

            CuboidDirection.EASY -> return Cuboid(
                worldName,
                this.lowerX,
                this.lowerY, this.lowerZ - amount,
                this.upperX,
                this.upperY,
                this.upperZ
            )

            CuboidDirection.WEST -> return Cuboid(
                worldName,
                this.lowerX,
                this.lowerY,
                this.lowerZ,
                this.upperX,
                this.upperY, this.upperZ + amount
            )

            CuboidDirection.DOWN -> return Cuboid(
                worldName,
                this.lowerX, this.lowerY - amount,
                this.lowerZ,
                this.upperX,
                this.upperY,
                this.upperZ
            )

            CuboidDirection.UP -> return Cuboid(
                worldName,
                this.lowerX,
                this.lowerY,
                this.lowerZ,
                this.upperX, this.upperY + amount,
                this.upperZ
            )

            else -> throw IllegalArgumentException("invalid direction $dir")
        }
    }

    /**
     * Shift the Cuboid in the given direction by the given amount.
     *
     * @param dir    the direction in which to shift
     * @param amount the number of blocks by which to shift
     *
     * @return a new Cuboid shifted by the given direction and amount
     */
    fun shift(dir: CuboidDirection, amount: Int): Cuboid {
        return expand(dir, amount).expand(dir.opposite(), -amount)
    }

    /**
     * Outset (grow) the Cuboid in the given direction by the given amount.
     *
     * @param dir    the direction in which to outset (must be HORIZONTAL, VERTICAL, or BOTH)
     * @param amount the number of blocks by which to outset
     *
     * @return a new Cuboid outset by the given direction and amount
     */
    fun outset(dir: CuboidDirection, amount: Int): Cuboid? {
        val c: Cuboid? = when (dir) {
            CuboidDirection.HORIZONTAL -> expand(CuboidDirection.NORTH, amount).expand(CuboidDirection.SOUTH, amount)
                .expand(CuboidDirection.EASY, amount)
                .expand(CuboidDirection.WEST, amount)

            CuboidDirection.VERTICAL -> expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount)
            CuboidDirection.BOTH -> outset(CuboidDirection.HORIZONTAL, amount)!!.outset(CuboidDirection.VERTICAL, amount)

            else -> throw IllegalArgumentException("invalid direction $dir")
        }
        return c
    }

    /**
     * Inset (shrink) the Cuboid in the given direction by the given amount. Equivalent to calling outset() with a
     * negative amount.
     *
     * @param dir    the direction in which to inset (must be HORIZONTAL, VERTICAL, or BOTH)
     * @param amount the number of blocks by which to inset
     *
     * @return a new Cuboid inset by the given direction and amount
     */
    fun inset(dir: CuboidDirection, amount: Int): Cuboid? {
        return outset(dir, -amount)
    }

    /**
     * Return true if the point at (x,y,z) is contained within this Cuboid.
     *
     * @param x the X coord
     * @param y the Y coord
     * @param z the Z coord
     *
     * @return true if the given point is within this Cuboid, false otherwise
     */
    fun contains(x: Int, y: Int, z: Int): Boolean {
        return x >= this.lowerX && x <= this.upperX && y >= this.lowerY && y <= this.upperY && z >= this.lowerZ && z <= this.upperZ
    }

    /**
     * Return true if the point at (x,z) is contained within this Cuboid.
     *
     * @param x the X coord
     * @param z the Z coord
     *
     * @return true if the given point is within this Cuboid, false otherwise
     */
    fun contains(x: Int, z: Int): Boolean {
        return x >= this.lowerX && x <= this.upperX && z >= this.lowerZ && z <= this.upperZ
    }

    /**
     * Check if the given Location is contained within this Cuboid.
     *
     * @param l the Location to check for
     *
     * @return true if the Location is within this Cuboid, false otherwise
     */
    fun contains(l: Location): Boolean {
        if (worldName != l.world.name) {
            return false
        }
        return contains(l.blockX, l.blockY, l.blockZ)
    }

    /**
     * Check if the given Block is contained within this Cuboid.
     *
     * @param b the Block to check for
     *
     * @return true if the Block is within this Cuboid, false otherwise
     */
    fun contains(b: Block): Boolean {
        return contains(b.location)
    }

    /**
     * Get the volume of this Cuboid.
     *
     * @return the Cuboid volume, in blocks
     */
    fun volume(): Int {
        return this.sizeX * this.sizeY * this.sizeZ
    }

    /**
     * Get the Cuboid representing the face of this Cuboid. The resulting Cuboid will be one block thick in the axis
     * perpendicular to the requested face.
     *
     * @param dir which face of the Cuboid to getInstance
     *
     * @return the Cuboid representing this Cuboid's requested face
     */
    fun getFace(dir: CuboidDirection): Cuboid {
        when (dir) {
            CuboidDirection.DOWN -> return Cuboid(
                worldName,
                this.lowerX,
                this.lowerY,
                this.lowerZ,
                this.upperX,
                this.lowerY,
                this.upperZ
            )

            CuboidDirection.UP -> return Cuboid(
                worldName,
                this.lowerX,
                this.upperY,
                this.lowerZ,
                this.upperX,
                this.upperY,
                this.upperZ
            )

            CuboidDirection.NORTH -> return Cuboid(
                worldName,
                this.lowerX,
                this.lowerY,
                this.lowerZ,
                this.lowerX,
                this.upperY,
                this.upperZ
            )

            CuboidDirection.SOUTH -> return Cuboid(
                worldName,
                this.upperX,
                this.lowerY,
                this.lowerZ,
                this.upperX,
                this.upperY,
                this.upperZ
            )

            CuboidDirection.EASY -> return Cuboid(
                worldName,
                this.lowerX,
                this.lowerY,
                this.lowerZ,
                this.upperX,
                this.upperY,
                this.lowerZ
            )

            CuboidDirection.WEST -> return Cuboid(
                worldName,
                this.lowerX,
                this.lowerY,
                this.upperZ,
                this.upperX,
                this.upperY,
                this.upperZ
            )

            else -> throw IllegalArgumentException("Invalid direction $dir")
        }
    }

    /**
     * Get the Cuboid big enough to hold both this Cuboid and the given one.
     *
     * @return a new Cuboid large enough to hold this Cuboid and the given Cuboid
     */
    fun getBoundingCuboid(other: Cuboid?): Cuboid {
        if (other == null) {
            return this
        }

        val xMin = min(this.lowerX, other.lowerX)
        val yMin = min(this.lowerY, other.lowerY)
        val zMin = min(this.lowerZ, other.lowerZ)
        val xMax = max(this.upperX, other.upperX)
        val yMax = max(this.upperY, other.upperY)
        val zMax = max(this.upperZ, other.upperZ)

        return Cuboid(worldName, xMin, yMin, zMin, xMax, yMax, zMax)
    }

    /**
     * Get a block relative to the lower NE point of the Cuboid.
     *
     * @param x the X coord
     * @param y the Y coord
     * @param z the Z coord
     *
     * @return the block at the given position
     */
    fun getRelativeBlock(x: Int, y: Int, z: Int): Block? {
        return this.world.getBlockAt(this.lowerX + x, this.lowerY + y, this.lowerZ + z)
    }

    /**
     * Get a block relative to the lower NE point of the Cuboid in the given World. This version of getRelativeBlock()
     * should be used if being called many times, to avoid excessive calls to getWorld().
     *
     * @param w the World
     * @param x the X coord
     * @param y the Y coord
     * @param z the Z coord
     *
     * @return the block at the given position
     */
    fun getRelativeBlock(w: World, x: Int, y: Int, z: Int): Block? {
        return w.getBlockAt(this.lowerX + x, this.lowerY + y, this.lowerZ + z)
    }

    val chunks: MutableList<Chunk?>
        /**
         * Get a list of the chunks which are fully or partially contained in this cuboid.
         *
         * @return a list of Chunk objects
         */
        get() {
            val chunks: MutableList<Chunk?> = ArrayList<Chunk?>()

            val w = this.world

            // These operators getInstance the lower bound of the chunk, by complementing 0xf (15) into 16
            // and using an OR gate on the integer coordinate
            val x1 = this.lowerX and 0xf.inv()
            val x2 = this.upperX and 0xf.inv()
            val z1 = this.lowerZ and 0xf.inv()
            val z2 = this.upperZ and 0xf.inv()

            var x = x1
            while (x <= x2) {
                var z = z1
                while (z <= z2) {
                    chunks.add(w.getChunkAt(x shr 4, z shr 4))
                    z += 16
                }
                x += 16
            }

            return chunks
        }

    val walls: Array<Cuboid?>
        /**
         * @return horizontal walls of the cuboid
         */
        get() = arrayOf<Cuboid?>(
            getFace(CuboidDirection.NORTH),
            getFace(CuboidDirection.SOUTH),
            getFace(CuboidDirection.WEST),
            getFace(CuboidDirection.EASY)
        )

    /**
     * @return read-only location iterator
     */
    override fun iterator(): MutableIterator<Location?> {
        return LocationCuboidIterator(
            this.world,
            this.lowerX,
            this.lowerY,
            this.lowerZ,
            this.upperX,
            this.upperY,
            this.upperZ
        )
    }

    override fun toString(): String {
        return "Cuboid: " + worldName + "," + this.lowerX + "," + this.lowerY + "," + this.lowerZ + "=>" + this.upperX + "," + this.upperY + "," + this.upperZ
    }

    class LocationCuboidIterator(
        private val w: World?,
        private val baseX: Int,
        private val baseY: Int,
        private val baseZ: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) : MutableIterator<Location?> {
        private var x: Int
        private var y: Int
        private var z = 0
        private val sizeX: Int
        private val sizeY: Int
        private val sizeZ: Int

        init {
            sizeX = abs(x2 - baseX) + 1
            sizeY = abs(y2 - baseY) + 1
            sizeZ = abs(z2 - baseZ) + 1
            y = z
            x = y
        }

        override fun hasNext(): Boolean {
            return x < sizeX && y < sizeY && z < sizeZ
        }

        override fun next(): Location {
            val b = Location(w, (baseX + x).toDouble(), (baseY + y).toDouble(), (baseZ + z).toDouble())
            if (++x >= sizeX) {
                x = 0
                if (++y >= sizeY) {
                    y = 0
                    ++z
                }
            }
            return b
        }

        override fun remove() {
        }
    }
}
