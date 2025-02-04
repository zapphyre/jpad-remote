#include <iostream>
#include <string>
#include <fcntl.h>
#include <unistd.h>
#include <linux/joystick.h>
#include <sys/stat.h>
#include <cstring>
#include <nlohmann/json.hpp>

using json = nlohmann::json;

// Function to get the number of axes and buttons for a joystick device
json getJoystickInfo(const std::string& device_path) {
    json result = json::array();
    int fd = open(device_path.c_str(), O_RDONLY | O_NONBLOCK);
    if (fd < 0) {
        std::cerr << "Failed to open " << device_path << std::endl;
        return result; // Return empty array on error
    }

    char name[128];
    if (ioctl(fd, JSIOCGNAME(sizeof(name)), name) < 0) {
        strncpy(name, "Unknown", sizeof(name));
    }

    __u32 version;
    __u8 axes;
    __u8 buttons;
    if (ioctl(fd, JSIOCGVERSION, &version) < 0 ||
        ioctl(fd, JSIOCGAXES, &axes) < 0 ||
        ioctl(fd, JSIOCGBUTTONS, &buttons) < 0) {
        std::cerr << "Failed to get joystick info for " << device_path << std::endl;
        close(fd);
        return result; // Return empty array on error
    }

    json device_info = {
        {"device", device_path},
        {"buttons", static_cast<int>(buttons)},
        {"axes", static_cast<int>(axes)},
        {"name", name}
    };
    result.push_back(device_info);
    close(fd);
    return result;
}

int main() {
    json all_devices = json::array();

    for (int i = 0; i < 32; ++i) {
        std::string device_path = "/dev/input/js" + std::to_string(i);
        struct stat buffer;
        if (stat(device_path.c_str(), &buffer) == 0) {
            json device_info = getJoystickInfo(device_path);
            if (!device_info.empty()) {
                all_devices.insert(all_devices.end(), device_info.begin(), device_info.end());
            }
        } else {
            // Device does not exist, continue to the next
            continue;
        }
    }

    // Output the JSON string
    std::cout << all_devices.dump(4) << std::endl;

    return 0;
}
