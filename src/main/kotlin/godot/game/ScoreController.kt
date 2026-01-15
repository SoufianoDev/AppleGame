package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node
import jdk.internal.org.jline.utils.Colors.s


@RegisterClass
class ScoreController : Node() {

	// ================= CONSTANTS & FORMATTING LOGIC =================
	object Scl {
		const val K = 1_000
		const val M = 1_000_000
		const val B = 1_000_000_000
		const val MAX_SCL = B
	}

	object Sfx {
		const val K = "K"
		const val M = "M"
		const val B = "B"
	}

	object Plyr {
		const val CHEATER = "--"
	}

	private var _currentScore: Int = 0

	@RegisterFunction
	override fun _ready() {
		_currentScore = 0
	}

	private fun fmtScore(sc: Int): String {
		val sf = sc.toLong()
		if (sf > Scl.MAX_SCL) {
			return Plyr.CHEATER
		}

		fun fs(scl: Int, sfx: String): String =
			if (sf % scl == 0L) "${sf / scl}$sfx"
			else "${"%.1f".format(sf.toDouble() / scl)}$sfx"

		return when {
			sf >= Scl.B -> fs(Scl.B, Sfx.B)
			sf >= Scl.M -> fs(Scl.M, Sfx.M)
			sf >= Scl.K -> fs(Scl.K, Sfx.K)
			else -> sf.toString()
		}
	}


	@RegisterFunction
	fun addScore(amount: Int) {
		_currentScore += amount
		_currentScore = _currentScore.coerceAtLeast(0)
	}

	
	@RegisterFunction
	fun setScore(value: Int) {
		_currentScore = value
	}

	@RegisterFunction
	fun getScore(): Int {
		return _currentScore
	}

	@RegisterFunction
	fun getFormattedScore(): String {
		return fmtScore(_currentScore)
	}

	@RegisterFunction
	fun resetScore() {
		_currentScore = 0
	}
}
