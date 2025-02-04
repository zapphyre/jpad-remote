#include <SDL2/SDL.h>
#include <stdio.h>
#include <string.h>

int main(int argc, char *argv[]) {
    if (SDL_Init(SDL_INIT_GAMECONTROLLER) < 0) {
        printf("SDL could not initialize! SDL_Error: %s\n", SDL_GetError());
        return 1;
    }

    // Load mappings from gamecontrollerdb.txt
    char path[1024];
    snprintf(path, sizeof(path), "%s/gamecontrollerdb.txt", SDL_GetBasePath());
    if (SDL_GameControllerAddMappingsFromFile(path) < 0) {
        printf("Failed to load controller mappings: %s\n", SDL_GetError());
        return 1; // Exit if we can't load mappings
    }

    int num_joysticks = SDL_NumJoysticks();
    for (int i = 0; i < num_joysticks; ++i) {
        if (SDL_IsGameController(i)) {
            SDL_GameController *controller = SDL_GameControllerOpen(i);
            if (controller) {
                char *mapping = SDL_GameControllerMapping(controller);
                if (mapping) {
                    printf("%s\n", mapping);
                    SDL_free(mapping);
                } else {
                    printf("NOT_FOUND\n");
                }
                SDL_GameControllerClose(controller);
            }
        }
    }

    SDL_Quit();
    return 0;
}
