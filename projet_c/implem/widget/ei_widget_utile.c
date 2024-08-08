#include "ei_widget_utile.h"

#include "ei_types.h"
#include "ei_impl_draw_utils.h"

ei_widget_t ei_widget_pixel(ei_widget_t root_widget, uint32_t pixel, ei_surface_t surface)
{
    if (root_widget != NULL) {
        uint32_t pick_color = ei_map_rgba(surface, *root_widget->pick_color);
        if (pick_color == pixel) {
            return root_widget;
        }else{
            ei_widget_t current = root_widget->children_head;
            while(current != NULL){
                ei_widget_t widget = ei_widget_pixel(current, pixel, surface);
                if(widget != NULL){
                    return widget;
                }else{
                    current = current->next_sibling;
                }
            }
        }
    }
    return NULL;
}