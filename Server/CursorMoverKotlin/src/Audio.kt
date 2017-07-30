import java.util.ArrayList

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.BooleanControl
import javax.sound.sampled.CompoundControl
import javax.sound.sampled.Control
import javax.sound.sampled.Control.Type
import javax.sound.sampled.FloatControl
import javax.sound.sampled.Line
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.Mixer

fun main(args: Array<String>) {
    println(hierarchyInfo)
    println(masterOutputVolume)
}

fun setMasterOutputVolume(value: Float) {
    if (value < 0 || value > 1)
        throw IllegalArgumentException(
                "Volume can only be set to a value from 0 to 1. Given value is illegal: " + value)
    val line = masterOutputLine ?: throw RuntimeException("Master output port not found")
    val opened = open(line)
    try {
        val control = getVolumeControl(line) ?: throw RuntimeException("Volume control not found in master port: " + toString(line)!!)
        control.value = value
    } finally {
        if (opened) line.close()
    }
}

val masterOutputVolume: Float?
    get() {
        val line = masterOutputLine ?: return null
        val opened = open(line)
        try {
            val control = getVolumeControl(line) ?: return null
            return control.value
        } finally {
            if (opened) line.close()
        }
    }

fun setMasterOutputMute(value: Boolean) {
    val line = masterOutputLine ?: throw RuntimeException("Master output port not found")
    val opened = open(line)
    try {
        val control = getMuteControl(line) ?: throw RuntimeException("Mute control not found in master port: " + toString(line)!!)
        control.value = value
    } finally {
        if (opened) line.close()
    }
}

val masterOutputMute: Boolean?
    get() {
        val line = masterOutputLine ?: return null
        val opened = open(line)
        try {
            val control = getMuteControl(line) ?: return null
            return control.value
        } finally {
            if (opened) line.close()
        }
    }

val masterOutputLine: Line?
    get() {
        return mixers
                .flatMap { getAvailableOutputLines(it) }
                .firstOrNull { it.lineInfo.toString().contains("Master") }
    }

fun getVolumeControl(line: Line): FloatControl? {
    if (!line.isOpen) throw RuntimeException("Line is closed: " + toString(line)!!)
    return findControl(FloatControl.Type.VOLUME, *line.controls) as FloatControl?
}

fun getMuteControl(line: Line): BooleanControl? {
    if (!line.isOpen) throw RuntimeException("Line is closed: " + toString(line)!!)
    return findControl(BooleanControl.Type.MUTE, *line.controls) as BooleanControl?
}

private fun findControl(type: Type, vararg controls: Control): Control? {
    if (controls.isEmpty()) return null
    for (control in controls) {
        if (control.type == type) return control
        if (control is CompoundControl) {
            val member = findControl(type, *control.memberControls)
            if (member != null) return member
        }
    }
    return null
}

val mixers: List<Mixer>
    get() {
        val infos = AudioSystem.getMixerInfo()
        val mixers = ArrayList<Mixer>(infos.size)
        infos.mapTo(mixers) { AudioSystem.getMixer(it) }
        return mixers
    }

fun getAvailableOutputLines(mixer: Mixer): List<Line> {
    return getAvailableLines(mixer, mixer.targetLineInfo)
}

fun getAvailableInputLines(mixer: Mixer): List<Line> {
    return getAvailableLines(mixer, mixer.sourceLineInfo)
}

private fun getAvailableLines(mixer: Mixer, lineInfos: Array<Line.Info>): List<Line> {
    val lines = ArrayList<Line>(lineInfos.size)
    lineInfos.mapNotNullTo(lines) { getLineIfAvailable(mixer, it) }
    return lines
}

fun getLineIfAvailable(mixer: Mixer, lineInfo: Line.Info): Line? {
    try {
        return mixer.getLine(lineInfo)
    } catch (ex: LineUnavailableException) {
        return null
    }

}

val hierarchyInfo: String
    get() {
        val sb = StringBuilder()
        for (mixer in mixers) {
            sb.append("Mixer: ").append(toString(mixer)).append("\n")

            for (line in getAvailableOutputLines(mixer)) {
                sb.append("  OUT: ").append(toString(line)).append("\n")
                val opened = open(line)
                for (control in line.controls) {
                    sb.append("    Control: ").append(toString(control)).append("\n")
                    if (control is CompoundControl) {
                        for (subControl in control.memberControls) {
                            sb.append("      Sub-Control: ").append(toString(subControl)).append("\n")
                        }
                    }
                }
                if (opened) line.close()
            }

            for (line in getAvailableOutputLines(mixer)) {
                sb.append("  IN: ").append(toString(line)).append("\n")
                val opened = open(line)
                for (control in line.controls) {
                    sb.append("    Control: ").append(toString(control)).append("\n")
                    if (control is CompoundControl) {
                        for (subControl in control.memberControls) {
                            sb.append("      Sub-Control: ").append(toString(subControl)).append("\n")
                        }
                    }
                }
                if (opened) line.close()
            }

            sb.append("\n")
        }
        return sb.toString()
    }

fun open(line: Line): Boolean {
    if (line.isOpen) return false
    try {
        line.open()
    } catch (ex: LineUnavailableException) {
        return false
    }

    return true
}

fun toString(control: Control?): String? {
    if (control == null) return null
    return control.toString() + " (" + control.type.toString() + ")"
}

fun toString(line: Line?): String? {
    if (line == null) return null
    val info = line.lineInfo
    return info.toString()
}

fun toString(mixer: Mixer?): String? {
    if (mixer == null) return null
    val sb = StringBuilder()
    val info = mixer.mixerInfo
    sb.append(info.name)
    sb.append(" (").append(info.description).append(")")
    sb.append(if (mixer.isOpen) " [open]" else " [closed]")
    return sb.toString()
}
