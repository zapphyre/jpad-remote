#include <iostream>
#include <string>
#include <fcntl.h>
#include <unistd.h>
#include <linux/joystick.h>
#include <sys/stat.h>
#include <cstring>
#include <nlohmann/json.hpp>
#include <SDL2/SDL.h>
#include <SDL2/SDL_gamecontroller.h>

using json = nlohmann::json;

json getJoystickInfo(const std::string& device_path) {
    json result;
    int fd = open(device_path.c_str(), O_RDONLY | O_NONBLOCK);
    if (fd < 0) {
        std::cerr << "[ERROR] Failed to open " << device_path << std::endl;
        return result;
    }

    char name[128];
    if (ioctl(fd, JSIOCGNAME(sizeof(name)), name) < 0) {
        strncpy(name, "Unknown", sizeof(name));
    }

    __u8 axes, buttons;
    if (ioctl(fd, JSIOCGAXES, &axes) < 0 || ioctl(fd, JSIOCGBUTTONS, &buttons) < 0) {
        std::cerr << "[ERROR] Failed to get joystick info for " << device_path << std::endl;
        close(fd);
        return result;
    }

    if (SDL_Init(SDL_INIT_GAMECONTROLLER | SDL_INIT_JOYSTICK) < 0) {
        std::cerr << "[ERROR] SDL Init failed: " << SDL_GetError() << std::endl;
        close(fd);
        return result;
    }

    SDL_GameControllerAddMappingsFromFile("gamecontrollerdb.txt");

    int num_joysticks = SDL_NumJoysticks();
    int detected_index = -1;
    for (int i = 0; i < num_joysticks; i++) {
        if (std::string(SDL_JoystickNameForIndex(i)) == name) {
            detected_index = i;
            break;
        }
    }

    if (detected_index == -1) {
        std::cerr << "[ERROR] No matching joystick found for " << name << std::endl;
        SDL_Quit();
        close(fd);
        return result;
    }

    SDL_Joystick* joystick = SDL_JoystickOpen(detected_index);
    std::string mapping = "Unknown";

    if (joystick) {
        SDL_JoystickGUID guid = SDL_JoystickGetGUID(joystick);
        char guid_str[33];
        SDL_JoystickGetGUIDString(guid, guid_str, sizeof(guid_str));

        if (SDL_IsGameController(detected_index)) {
            SDL_GameController* controller = SDL_GameControllerOpen(detected_index);
            if (controller) {
                const char* controller_mapping = SDL_GameControllerMapping(controller);
                if (controller_mapping) {
                    mapping = controller_mapping;
                }
                SDL_GameControllerClose(controller);
            }
        }

        SDL_JoystickClose(joystick);
    }

    SDL_Quit();
    close(fd);

    return {
        {"device", device_path},
        {"buttons", static_cast<int>(buttons)},
        {"axes", static_cast<int>(axes)},
        {"name", name},
        {"mapping", mapping}
    };
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cerr << "[ERROR] Usage: " << argv[0] << " <device_path>" << std::endl;
        return 1;
    }

    std::string device_path = argv[1];
    struct stat buffer;
    if (stat(device_path.c_str(), &buffer) == 0) {
        json device_info = getJoystickInfo(device_path);
        if (!device_info.empty()) {
            std::cout << device_info.dump(4) << std::endl; // JSON output only
        } else {
            std::cerr << "[ERROR] No information found for device: " << device_path << std::endl;
            return 1;
        }
    } else {
        std::cerr << "[ERROR] Device does not exist: " << device_path << std::endl;
        return 1;
    }

    return 0;
}