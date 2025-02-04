package org.asmus.model;

import org.bbi.linuxjoy.LinuxJoystick;

import java.util.List;

public record ControllerDevice(List<ButtonNamePosition> axisMappings, List<ButtonNamePosition> buttonMappings,
                               LinuxJoystick joystick) {
}
