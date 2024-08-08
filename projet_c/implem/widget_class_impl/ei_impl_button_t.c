#include "ei_impl_button_t.h"
#include "ei_widget_configure.h"
#include "ei_utils.h"
#include "ei_draw.h"
#include "ei_impl_draw_utils.h"
#include "ei_widgetclass_extend.h"
#include "ei_event.h"
#include "hw_interface.h"
#include "ei_app_offscreen.h"
#include "ei_widget_utile.h"
#include "ei_widget_attributes.h"

/**
 * \brief	A function that allocates a block of memory that is big enough to store the
 *		attributes of a widget of a class: both the common attributes, that use
*		\ref ei_widget_struct_size bytes, and the specific attributes of the class.
*		After allocation, the function *must* initialize the memory to 0.
*
* @return			A block of memory big enough to represent a widget of this class,
* 				with all bytes set to 0.
*/
ei_widget_t button_allocfunc(){
    ei_impl_button_t* button = calloc(1,sizeof(ei_impl_button_t));
    return (ei_widget_t)button;
}

/**
 * \brief	A function that releases the memory used by a widget before it is destroyed.
 *		The memory used for the \ref ei_widget_t structure itself must *not* be freed by
*		the function. It frees the *fields* of the structure, if necessary.
*		Can be set to NULL in \ref ei_widgetclass_t if no memory is used by a class of widget.
*
* @param	widget		The widget which resources are to be freed.
*/
void button_releasefunc(ei_widget_t widget){
    ei_impl_button_t* button = (ei_impl_button_t*)widget;
    if(button->img != NULL){
        hw_surface_unlock(button->img);
        hw_surface_free(button->img);
    }
    free(button->img_rect);
    free(button->text);
}


/**
 * \brief	A function that draws a widget of a class.
 * 		The function must also draw the children of the widget.
 *
 * @param	widget		A pointer to the widget instance to draw.
 * @param	surface		A locked surface where to draw the widget. The actual location of the widget in the
 *				surface is stored in its "screen_location" field.
* @param	pick_surface	The picking offscreen.
* @param	clipper		If not NULL, the drawing is restricted within this rectangle
*				(expressed in the surface reference button).
*/
void button_drawfunc(ei_widget_t widget,
                    ei_surface_t		surface,
                    ei_surface_t		pick_surface,
                    ei_rect_t*		clipper){

    /* assert the widget is placed */
    if(widget->placer_params == NULL){return;}

    ei_impl_button_t* button = (ei_impl_button_t*)widget;

    /* button main background */
    if(button->relief == ei_relief_none || button->border_width == 0)
    {
        ei_line_t* button_smooth_rect = create_line_smooth_rect(button->widget.screen_location, button->corner_radius);
        ei_draw_polygon(surface, button_smooth_rect->points, button_smooth_rect->length, button->color, clipper);
        ei_draw_polygon(pick_surface, button_smooth_rect->points, button_smooth_rect->length, *(button->widget.pick_color), clipper);
        free_line(button_smooth_rect);
    } else {
        /* relief color */
        ei_color_t half_top_color;
        ei_color_t half_bottom_color;
        if(button->relief == ei_relief_raised){
            half_top_color = (ei_color_t){
                (button->color.red + 255)/2,
                (button->color.green + 255)/2,
                (button->color.blue + 255)/2,
                button->color.alpha};
            half_bottom_color = (ei_color_t){ 
                button->color.red / 2,
                button->color.green / 2,
                button->color.blue / 2,
                button->color.alpha };
        } else if (button->relief == ei_relief_sunken) {
            half_top_color = (ei_color_t){
                button->color.red / 2,
                button->color.green / 2,
                button->color.blue / 2,
                button->color.alpha };
            half_bottom_color = (ei_color_t){ 
                (button->color.red + 255)/2,
                (button->color.green + 255)/2,
                (button->color.blue + 255)/2,
                button->color.alpha};
        }

        /* button relief */
        ei_line_t* button_smooth_rect = create_line_smooth_rect(button->widget.screen_location, button->corner_radius);
        ei_line_rotate_polygon_representation(button_smooth_rect, button_smooth_rect->length*3/8);
        ei_line_t* half_rect = malloc(sizeof(ei_line_t));
        half_rect->length = (button_smooth_rect->length-1)/2 + 4;
        half_rect->points = malloc(half_rect->length*sizeof(ei_point_t));

        int middle_min = min(button->widget.screen_location.size.width, button->widget.screen_location.size.height)/2;
        ei_point_t middle_BL = ei_point(
            button->widget.screen_location.top_left.x + middle_min, 
            button->widget.screen_location.top_left.y + button->widget.screen_location.size.height - middle_min);
        ei_point_t middle_TR = ei_point(
            button->widget.screen_location.top_left.x + button->widget.screen_location.size.width - middle_min, 
            button->widget.screen_location.top_left.y + middle_min);

            /* top half */
        int decal = (button_smooth_rect->length-1)/2;
        for(int i = 0; i < half_rect->length - 3; i++){
            half_rect->points[i] = button_smooth_rect->points[i + decal];
        }
        half_rect->points[half_rect->length - 3] = middle_BL;
        half_rect->points[half_rect->length - 2] = middle_TR;
        half_rect->points[half_rect->length - 1] = half_rect->points[0];
        ei_draw_polygon(surface, half_rect->points, half_rect->length, half_top_color, clipper);


            /* bottom half */
        for(int i = 0; i < half_rect->length - 3; i++){
            half_rect->points[i] = button_smooth_rect->points[i];
        }
        half_rect->points[half_rect->length - 3] = middle_TR;
        half_rect->points[half_rect->length - 2] = middle_BL;
        half_rect->points[half_rect->length - 1] = half_rect->points[0];
        ei_draw_polygon(surface, half_rect->points, half_rect->length, half_bottom_color, clipper);
        
        free_line(half_rect);
        
        /* draw pick surface button */
        ei_draw_polygon(pick_surface, button_smooth_rect->points, button_smooth_rect->length, *(button->widget.pick_color), clipper);
        free_line(button_smooth_rect);

        /* button background */
        ei_line_t* button_inner_smooth_rect = create_line_smooth_rect(
                ei_rect(
                    ei_point(
                        button->widget.screen_location.top_left.x + button->border_width,
                        button->widget.screen_location.top_left.y + button->border_width),
                    ei_size(
                        max(0, button->widget.screen_location.size.width - 2*button->border_width),
                        max(0, button->widget.screen_location.size.height - 2*button->border_width))),
                max(0, button->corner_radius - button->border_width));
        ei_draw_polygon(surface, button_inner_smooth_rect->points, button_inner_smooth_rect->length, button->color, clipper);
        free_line(button_inner_smooth_rect);
    }


    /* add image */
    if(button->img != NULL){
        hw_surface_lock(button->img);
        ei_rect_t img_sample = ei_rect(ei_point_zero(), hw_surface_get_size(button->img));
        if(button->img_rect != NULL){
            img_sample = ei_intersect_rect(img_sample, *(button->img_rect));
        }

        ei_point_t where = get_point_from_anchor(button->img_anchor, widget->content_rect, img_sample.size);

        ei_rect_t sruface_constrain = ei_rect(ei_point_zero(), hw_surface_get_size(surface));
        if(clipper != NULL) {
            sruface_constrain = ei_intersect_rect(*clipper, sruface_constrain);
        }
        sruface_constrain = ei_intersect_rect(widget->content_rect, sruface_constrain);

        ei_rect_t dst_rect = ei_intersect_rect(sruface_constrain, ei_rect(where, img_sample.size));
        ei_rect_t src_rect = ei_intersect_rect(img_sample, ei_rect(ei_point_add(img_sample.top_left, ei_point_sub(sruface_constrain.top_left, where)), sruface_constrain.size));

        ei_copy_surface(surface, &dst_rect, button->img, &src_rect, true);

        hw_surface_unlock(button->img);
    }

    /* add text */
    if(button->text != NULL && strcmp(button->text, "") != 0){
        ei_size_t text_size = {0,0};
        hw_text_compute_size(button->text, button->text_font, &text_size.width, &text_size.height);
        ei_point_t where = get_point_from_anchor(button->text_anchor, ei_rect(button->widget.screen_location.top_left, widget->screen_location.size), text_size);
        ei_draw_text(surface, &where , button->text, button->text_font, button->text_color, clipper);
    }

    /* draw children */
    ei_widget_t current = widget->children_head;
    while (current != NULL){
        current->wclass->drawfunc(current, surface, pick_surface, &(button->widget.content_rect));
        current = current->next_sibling;
    }
}





/**
 * \brief	A function that sets the default values for a widget of this class.
 *
 * @param	widget		A pointer to the widget instance to initialize.
 */
void button_setdefaultsfunc(ei_widget_t widget){
    ei_impl_button_t* button = (ei_impl_button_t*)widget;

    int min_size = 2 * (k_default_button_corner_radius + k_default_button_border_width);
    button->widget.requested_size = ei_size(min_size, min_size);
    button->color = ei_default_background_color;
    button->border_width = k_default_button_border_width;
    button->corner_radius = k_default_button_corner_radius;
    button->relief = ei_relief_none;
    // text
    button->text = NULL;
    button->text_font = ei_default_font;
    button->text_color = ei_font_default_color;
    button->text_anchor = ei_anc_center;
    // image
    button->img = NULL;
    button->img_rect = NULL;
    button->img_anchor = ei_anc_center;
    // function
    button->callback = NULL;
    button->user_param = NULL;

}

/**
 * \brief 	A function that is called to notify the widget that its geometry has been modified
 *		by its geometry manager. Can set to NULL in \ref ei_widgetclass_t.
*		The new location can be retrieved by \ref ei_widget_get_screen_location.
*
* @param	widget		The widget instance to notify of a geometry change.
*/
void button_geomnotifyfunc(ei_widget_t widget){
    ei_impl_button_t * button = (ei_impl_button_t *)widget;

    widget->content_rect = widget->screen_location;
    widget->content_rect.top_left.x  += button->border_width;
    widget->content_rect.top_left.y  += button->border_width;
    widget->content_rect.size.width  -= 2*button->border_width;
    widget->content_rect.size.height -= 2*button->border_width;
}

/**
 * @brief	A function that is called in response to an event. This function
 *		is internal to the library. It implements the generic behavior of
*		a widget (for example a button looks sunken when clicked)
*
* @param	widget		The widget for which the event was generated.
* @param	event		The event containing all its parameters (type, etc.)
*
* @return			A boolean telling if the event was consumed by the callback or not.
*				If true, the library does not try to call other callbacks for this
*				event. If false, the library will call the next callback registered
*				for this event, if any.
*				Note: The callback may execute many operations and still return
*				false, or return true without having done anything.
*/
bool button_handlefunc(ei_widget_t widget,
                      struct ei_event_t*	event){

        /* assert the widget is placed */
    if(widget->placer_params == NULL){return false;}

    ei_impl_button_t* button = (ei_impl_button_t*)widget;

    // button event (mouse)
    if((event->type == ei_ev_mouse_buttondown || event->type == ei_ev_mouse_buttonup) && event->param.mouse.button == ei_mouse_button_left){
        uint32_t mouse_id = ei_widget_pick_id_at(&(event->param.mouse.where));
        if(event->type == ei_ev_mouse_buttondown && mouse_id == widget->pick_id){
                ei_event_set_active_widget(widget);
                button->relief = ei_relief_sunken;
                return true;
        }
        if(event->type == ei_ev_mouse_buttonup && ei_event_get_active_widget() == widget){
            ei_event_set_active_widget(NULL);
            button->relief = ei_relief_raised;
            if(mouse_id == widget->pick_id){
                button->callback(widget, event, button->user_param);
            }
            return true;
        }
    }

        // children event
    bool is_consumed = false;
    ei_widget_t current = widget->children_head;
    while (current != NULL && !is_consumed){
        is_consumed = current->wclass->handlefunc(current, event);
        current = current->next_sibling;
    }
    return is_consumed;               
}
