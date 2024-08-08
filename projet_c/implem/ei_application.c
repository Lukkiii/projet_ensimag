
#include "ei_application.h"

#include "ei_impl_button_t.h"
#include "ei_impl_frame_t.h"
#include "ei_impl_toplevel_t.h"
#include "ei_widget_configure.h"
#include "ei_event.h"
#include "ei_widgetclass_extend.h"
#include "ei_impl_draw_utils.h"

static ei_widget_t app_root;
static ei_surface_t main_window;
static ei_surface_t offscreen;
ei_linked_rect_t *new_list_rect;
bool app_app_quit_request = false;

/**
 * \brief	Creates an application.
 *		<ul>
 *			<li> initializes the hardware (calls \ref hw_init), </li>
 *			<li> registers all classes of widget, </li>
 *			<li> creates the root window (either in a system window, or the entire screen), </li>
 *			<li> creates the root widget to access the root window. </li>
 *		</ul>
 *
 * @param	main_window_size	If "fullscreen is false, the size of the root window of the
 *					application.
 *					If "fullscreen" is true, the current monitor resolution is
 *					used as the size of the root window. \ref hw_surface_get_size
 *					can be used with \ref ei_app_root_surface to get the size.
 * @param	fullScreen		If true, the root window is the entire screen. Otherwise, it
 *					is a system window.
 */
void ei_app_create(ei_size_t main_window_size, bool fullscreen){
    hw_init();

    ei_widgetclass_t* class_button = malloc(sizeof(ei_widgetclass_t));
    *class_button = ei_class_button;
    ei_widgetclass_register(class_button);

    ei_widgetclass_t* class_frame = malloc(sizeof(ei_widgetclass_t));
    *class_frame = ei_class_frame;
    ei_widgetclass_register(class_frame);

    ei_widgetclass_t* class_toplevel = malloc(sizeof(ei_widgetclass_t));
    *class_toplevel = ei_class_toplevel;
    ei_widgetclass_register(class_toplevel);

    main_window = hw_create_window(main_window_size, fullscreen);
    offscreen = hw_surface_create(main_window, main_window_size, true);
    app_root = ei_widget_create("frame", NULL, NULL, NULL);
    ei_frame_configure(app_root, &main_window_size, &ei_default_background_color, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
    ei_place(app_root, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL);
    

    new_list_rect = malloc(sizeof(ei_linked_rect_t));
    *new_list_rect = (ei_linked_rect_t){ei_rect(ei_point(0,0), main_window_size), NULL};
}

/**
 * \brief	Releases all the resources of the application, and releases the hardware
 *		(ie. calls \ref hw_quit).
 */
void ei_app_free(void){
    hw_surface_free(offscreen);
    hw_surface_free(main_window);
    ei_widget_destroy(app_root);
    ei_widgetclass_free_register();
    hw_quit();
}




/**
 * \brief	Runs the application: enters the main event loop. Exits when
 *		\ref ei_app_quit_request is called.
 */
void ei_app_run(void){
    ei_event_t event;
    bool event_consumed = true;
    event.type = ei_ev_none;
    while (event.type != ei_ev_close)
    {
        hw_surface_lock(main_window);
        hw_surface_lock(offscreen);

        switch (event.type)
        {
        case ei_ev_mouse_buttondown:
        case ei_ev_mouse_buttonup:
        case ei_ev_mouse_move:
        case ei_ev_keydown:
        case ei_ev_keyup:
        case ei_ev_app:
            event_consumed = app_root->wclass->handlefunc(app_root, &event);
            if(!event_consumed){
                event_consumed = ei_event_get_default_handle_func()(&event);
            }
            break;
        case ei_ev_exposed:
            break;
        default:
            break;
        }

        if(event_consumed){
            app_root->wclass->drawfunc(app_root, main_window, offscreen, NULL);
        }

        hw_surface_unlock(offscreen);
        hw_surface_unlock(main_window);
        hw_surface_update_rects(main_window, new_list_rect);
        
        hw_event_wait_next(&event);
        event_consumed = false;
    }
}

/**
 * \brief	Adds a rectangle to the list of rectangles that must be updated on screen. The real
 *		update on the screen will be done at the right moment in the main loop.
 *
 * @param	rect		The rectangle to add, expressed in the root window coordinates.
 *				A copy is made, so it is safe to release the rectangle on return.
 */
void ei_app_invalidate_rect(const ei_rect_t* rect){
    if (new_list_rect != NULL){
        ei_linked_rect_t *current = new_list_rect;
        while(current->next){
            ei_rect_t intersect_rect = ei_intersect_rect(current->rect, *rect);
            if(intersect_rect.size.width != 0 && intersect_rect.size.height != 0){
                current->rect = ei_combination_rect(current->rect, *rect);
                return;
            }
            current = current->next;
        }
        ei_rect_t intersect_rect = ei_intersect_rect(current->rect, *rect);
        if (intersect_rect.size.width != 0 && intersect_rect.size.height != 0){
            current->rect = ei_combination_rect(current->rect, *rect);
            return;
        }
        current->next = calloc(1, sizeof(ei_linked_rect_t));
        current = current->next;
        current->rect = *rect;
    }else{
        new_list_rect = calloc(1, sizeof(ei_linked_rect_t));
        new_list_rect->rect = *rect;
    }
}

/**
 * \brief	Tells the application to quit. Is usually called by an event handler (for example
 *		when pressing the "Escape" key).
 */
void ei_app_quit_request(void){
    app_app_quit_request = true;
}

/**
 * \brief	Returns the "root widget" of the application: a "frame" widget that span the entire
 *		root window.
 *
 * @return 			The root widget.
 */
ei_widget_t ei_app_root_widget(void)
{
    return app_root;
}

/**
 * \brief	Returns the surface of the root window. Can be used to create surfaces with similar
 * 		r, g, b channels.
 *
 * @return 			The surface of the root window.
 */
ei_surface_t ei_app_root_surface(void){
    return main_window;
}

/**
 * @brief Return app offscreen
*/

ei_surface_t ei_app_offscreen(){
    return offscreen;
}