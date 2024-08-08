#include "ei_types.h"
#include "ei_event.h"
#include "ei_implementation.h"

/**
 * @brief	Returns the widget that is at a given pixel on offscreen.
 *
 * @param	root_widget		The widget given(start at the root widget).
 * @param	pixel		The pixel in offscreen given.
 * @param   surface The surface picked
 *
 * @return			The top-most widget at this pixel, or NULL if there is no widget
 *				at this pixel (except for the root widget).
 */
ei_widget_t ei_widget_pixel(ei_widget_t root_widget, uint32_t pixel, ei_surface_t surface);



uint32_t ei_widget_pick_id_at(ei_point_t* where);