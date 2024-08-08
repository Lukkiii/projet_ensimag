#include "ei_widget.h"
#include "ei_application.h"
#include "ei_widget_utile.h"
#include "ei_impl_draw_utils.h"
#include "ei_app_offscreen.h"

static uint32_t current_id = 0;

ei_widget_t ei_widget_create(ei_const_string_t class_name,
                             ei_widget_t parent,
                             ei_user_param_t user_data,
                             ei_widget_destructor_t destructor){
    
    ei_widgetclass_t *widgetclass = ei_widgetclass_from_name(class_name);
    ei_widget_t widget = widgetclass->allocfunc();
    widget->wclass = widgetclass;
    
    widget->wclass->setdefaultsfunc(widget);
    widget->content_rect = widget->screen_location;
    widget->pick_id = current_id;
    current_id++;
    widget->pick_color = (ei_color_t*)&widget->pick_id;
    widget->placer_params = NULL;

    widget->user_data = user_data;
    widget->destructor = destructor;

    widget->parent = parent;
    widget->children_head = NULL;
    widget->children_tail = NULL;
    widget->next_sibling = NULL;

    if (parent != NULL) {
        if(parent->children_head == NULL){
            parent->children_head = widget;
            parent->children_tail = widget;
            widget->prev_sibling = NULL;
        } else {
            widget->prev_sibling = parent->children_tail;
            widget->prev_sibling->next_sibling = widget;
            parent->children_tail = widget;
        }
    }

    
    return widget;
}



void ei_widget_destroy (ei_widget_t		widget){
    // TODO: Removes the widget from the screen if it is currently displayed.

    ei_widget_t current_widget = widget->children_head;
    ei_widget_t tmp;
    while (current_widget != NULL)
    {
        tmp = current_widget->next_sibling;
        ei_widget_destroy(current_widget);
        current_widget = tmp;
    }
    
    if(widget->destructor != NULL){
        widget->destructor(widget);
    }

    if(widget->parent != NULL){
        if(widget->next_sibling == NULL && widget->prev_sibling == NULL){
            widget->parent->children_head = NULL;
            widget->parent->children_tail = NULL;
        } else if (widget->next_sibling == NULL && widget->prev_sibling != NULL){
            widget->parent->children_tail = widget->prev_sibling;
            widget->prev_sibling->next_sibling = NULL;
        } else if (widget->next_sibling != NULL && widget->prev_sibling == NULL){
            widget->parent->children_head = widget->next_sibling;
            widget->next_sibling->prev_sibling = NULL;
        } else {
            widget->next_sibling->prev_sibling = widget->prev_sibling;
            widget->prev_sibling->next_sibling = widget->next_sibling;
        }
    }


    widget->wclass->releasefunc(widget);
    ei_placer_forget(widget);
    free(widget);
}

bool ei_widget_is_displayed(ei_widget_t widget){
    return false;
}

uint32_t ei_widget_pick_id_at(ei_point_t* where){

    // get pixel color
    ei_rect_t rect_offscreen = hw_surface_get_rect(ei_app_offscreen());
    uint8_t *pixel = hw_surface_get_buffer(ei_app_offscreen());
    ei_color_t* color = (ei_color_t*)&pixel[4 * rect_offscreen.size.width * where->y + 4 * where->x];

    // decode color encoding
    int ir, ig, ib, ia;
    uint32_t color32 = 0;
    hw_surface_get_channel_indices(ei_app_offscreen(), &ir, &ig, &ib, &ia);
    set_to_channel_not_used(ir, ig, ib, &ia);
    color32 |= color->red << (ir * 8);
    color32 |= color->green << (ig * 8);
    color32 |= color->blue << (ib * 8);
    color32 |= color->alpha << (ia * 8);

    return color32;
}



ei_widget_t	ei_widget_pick(ei_point_t* where){
    /*
    uint32_t* pixel = (uint32_t*)hw_surface_get_buffer(pick_surface);
    ei_size_t pick_surface_size = hw_surface_get_size(pick_surface);

    //get the address of the pixel for the pick_id
    pixel += pick_surface_size.width * where->y + where->x;

    ei_widget_t root_widget = ei_app_root_widget();
    ei_widget_t widget = ei_widget_pixel(root_widget, *pixel, pick_surface);

    return widget;*/
    return NULL;
}