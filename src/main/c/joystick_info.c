#include <SDL2/SDL.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jansson.h>

// Function to get joystick info and add to JSON array
void getJoystickInfo(json_t *gamepads, const char *dbPath) {
    // Load mappings from the database file
    if (SDL_GameControllerAddMappingsFromFile(dbPath) == -1) {
        fprintf(stderr, "Could not load mappings from %s: %s\n", dbPath, SDL_GetError());
        return;
    }

    int numJoysticks = SDL_NumJoysticks();
    for (int i = 0; i < numJoysticks; i++) {
        if (SDL_IsGameController(i)) {
            SDL_GameController *controller = SDL_GameControllerOpen(i);
            if (controller) {
                json_t *gamepad = json_object();

                // Name
                const char *name = SDL_GameControllerName(controller);
                json_object_set_new(gamepad, "name", json_string(name ? name : "Unknown"));

                // Path
                char path[64];
                snprintf(path, sizeof(path), "/dev/input/js%d", i);
                json_object_set_new(gamepad, "path", json_string(path));

                // Axes and Buttons
                SDL_Joystick *joystick = SDL_GameControllerGetJoystick(controller);
                int numAxes = SDL_JoystickNumAxes(joystick);
                int numButtons = SDL_JoystickNumButtons(joystick);
                json_object_set_new(gamepad, "axes", json_integer(numAxes));
                json_object_set_new(gamepad, "buttons", json_integer(numButtons));

                // Mapping - Use SDL_GameControllerMapping to get the actual mapping string
                char *mapping = SDL_GameControllerMapping(controller);
                if (mapping) {
                    json_object_set_new(gamepad, "mapping", json_string(mapping));
                    SDL_free(mapping); // Free the string after use
                } else {
                    json_object_set_new(gamepad, "mapping", json_string("Mapping not available"));
                }

                json_array_append_new(gamepads, gamepad);
                SDL_GameControllerClose(controller);
            } else {
                fprintf(stderr, "Could not open game controller %d: %s\n", i, SDL_GetError());
            }
        }
    }
}

int main(int argc, char *argv[]) {
        // Initialize SDL
        if (SDL_Init(SDL_INIT_GAMECONTROLLER) < 0) {
            fprintf(stderr, "SDL could not initialize! SDL_Error: %s\n", SDL_GetError());
            return 1;
        }

        json_t *gamepads = json_array();
        getJoystickInfo(gamepads, "gamecontrollerdb.txt");


    // Output JSON
    char *json_str = json_dumps(gamepads, JSON_INDENT(4));
    printf("%s\n", json_str);

    // Clean up
    json_decref(gamepads);
    free(json_str);

        SDL_Quit();
        return 0;
}