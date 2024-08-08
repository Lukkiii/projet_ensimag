#include "ei_impl_frame_t.h"
#include "ei_utils.h"
#include "ei_widget_attributes.h"
#include "ei_impl_draw_utils.h"

/**
 * \brief	A function that allocates a block of memory that is big enough to store the
 *		attributes of a widget of a class: both the common attributes, that use
 *		\ref ei_widget_struct_size bytes, and the specific attributes of the class.
 *		After allocation, the function *must* initialize the memory to 0.
 *
 * @return			A block of memory big enough to represent a widget of this class,
 * 				with all bytes set to 0.
 */
ei_widget_t
frame_allocfunc()
{
    ei_impl_frame_t* frame = calloc(1, sizeof(ei_impl_frame_t));
    return (ei_widget_t)frame;
}

/**
 * \brief	A function that releases the memory used by a widget before it is destroyed.
 *		The memory used for the \ref ei_widget_t structure itself must *not* be freed by
 *		the function. It frees the *fields* of the structure, if necessary.
 *		Can be set to NULL in \ref ei_widgetclass_t if no memory is used by a class of widget.
 *
 * @param	widget		The widget which resources are to be freed.
 */
void frame_releasefunc(ei_widget_t widget){
    ei_impl_frame_t *frame = (ei_impl_frame_t *)widget;
    if(frame->img != NULL){
        hw_surface_unlock(frame->img);
        hw_surface_free(frame->img);
    }
    free(frame->img_rect);
    free(frame->text);
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
 *				(expressed in the surface reference frame).
 */
void frame_drawfunc(ei_widget_t widget,
                   ei_surface_t surface,
                   ei_surface_t pick_surface,
                   ei_rect_t *clipper){

        /* assert the widget is placed */
    if(widget->placer_params == NULL){return;}
    
    ei_impl_frame_t *frame = (ei_impl_frame_t *)widget;

    /* rect */
    ei_rect_t rect_location = frame->widget.screen_location;
    size_t point_array_size = 5;
    ei_point_t ptr[5];

    ei_point_t NW = rect_location.top_left;
    ei_point_t SE = ei_point_add(NW, ei_size_as_point(rect_location.size));
    ei_point_t NE = ei_point(SE.x, NW.y);
    ei_point_t SW = ei_point(NW.x, SE.y);
    ptr[0] = NW;
    ptr[1] = NE;
    ptr[2] = SE;
    ptr[3] = SW;
    ptr[4] = NW;

    ei_draw_polygon(surface, ptr, point_array_size, frame->color, clipper);

    /* colors for the relief */
    ei_color_t color_left_top = {(frame->color.red + 255)/2,
                                 (frame->color.green + 255)/2,
                                 (frame->color.blue + 255)/2,
                                 frame->color.alpha};
    ei_color_t color_right_bottom = { frame->color.red / 2,
                                      frame->color.green / 2,
                                      frame->color.blue / 2,
                                      frame->color.alpha };
    if (frame->relief == ei_relief_sunken){
        color_left_top = color_right_bottom;
        color_right_bottom = frame->color;
    }

    /* top border */
    size_t point_array_size_top_border = 7;
    ei_point_t ptr_top_border[7];
    ei_point_t NW_relief = {NW.x-frame->border_width, NW.y-frame->border_width};//top_left
    ei_point_t SE_relief = {SE.x+frame->border_width, SE.y+frame->border_width};//bottom left 
    ei_point_t NE_relief = ei_point(SE_relief.x, NW_relief.y);//top right 
    ei_point_t SW_relief = ei_point(NW_relief.x, SE_relief.y);//bottom right
    ptr_top_border[0] = NW_relief;
    ptr_top_border[1] = NE_relief;
    ptr_top_border[2] = NE;
    ptr_top_border[3] = NW;
    ptr_top_border[4] = SW;
    ptr_top_border[5] = SW_relief;
    ptr_top_border[6] = NW_relief;
    ei_draw_polygon(surface, ptr_top_border, point_array_size_top_border, color_left_top, clipper);

    /* bottom border */
    size_t point_array_size_bottom_border = 7;
    ei_point_t ptr_bottom_border[7];
    ptr_bottom_border[0] = NE_relief;
    ptr_bottom_border[1] = SE_relief;
    ptr_bottom_border[2] = SW_relief;
    ptr_bottom_border[3] = SW;
    ptr_bottom_border[4] = SE;
    ptr_bottom_border[5] = NE;
    ptr_bottom_border[6] = NE_relief;
    ei_draw_polygon(surface, ptr_bottom_border, point_array_size_bottom_border, color_right_bottom, clipper);

    /* text */
    if (frame->text != NULL && strcmp(frame->text, "") != 0){
        ei_size_t text_size = {0, 0};
        hw_text_compute_size(frame->text, frame->text_font, &text_size.width, &text_size.height);
        ei_point_t where = get_point_from_anchor(frame->text_anchor, ei_rect(frame->widget.screen_location.top_left, widget->screen_location.size), text_size);
        ei_draw_text(surface, &where, frame->text, frame->text_font, frame->text_color, &(frame->widget.content_rect));
    }

    /* image */
    else if (frame->img != NULL){
        hw_surface_lock(frame->img);
        ei_rect_t img_sample = ei_rect(ei_point_zero(), hw_surface_get_size(frame->img));
        if(frame->img_rect != NULL){
            img_sample = ei_intersect_rect(img_sample, *(frame->img_rect));
        }

        ei_point_t where = get_point_from_anchor(frame->img_anchor, widget->content_rect, img_sample.size);

        ei_rect_t sruface_constrain = ei_rect(ei_point_zero(), hw_surface_get_size(surface));
        if(clipper != NULL) {
            sruface_constrain = ei_intersect_rect(*clipper, sruface_constrain);
        }
        sruface_constrain = ei_intersect_rect(widget->content_rect, sruface_constrain);

        ei_rect_t dst_rect = ei_intersect_rect(sruface_constrain, ei_rect(where, img_sample.size));
        ei_rect_t src_rect = ei_intersect_rect(img_sample, ei_rect(ei_point_add(img_sample.top_left, ei_point_sub(sruface_constrain.top_left, where)), sruface_constrain.size));

        ei_copy_surface(surface, &dst_rect, frame->img, &src_rect, true);

        hw_surface_unlock(frame->img);
    }

    // draw children
    ei_widget_t current = widget->children_tail;
    while (current != NULL){
        current->wclass->drawfunc(current, surface, pick_surface, &(widget->content_rect));
        current = current->prev_sibling;
    }
}

/**
 * \brief	A function that sets the default values for a widget of this class.
 *
 * @param	widget		A pointer to the widget instance to initialize.
 */
void frame_setdefaultsfunc(ei_widget_t widget){
    ei_impl_frame_t *frame = (ei_impl_frame_t *)widget;
    
    frame->widget.requested_size = ei_size_zero();
    frame->border_width = 0;
    frame->color = ei_default_background_color;
    frame->relief = ei_relief_none;

    frame->text = NULL;
    frame->text_font = ei_default_font;
    frame->text_color = ei_font_default_color;
    frame->text_anchor = ei_anc_center;

    frame->img = NULL;
    frame->img_rect = NULL;
    frame->img_anchor = ei_anc_center;
}

/**
 * \brief 	A function that is called to notify the widget that its geometry has been modified
 *		by its geometry manager. Can set to NULL in \ref ei_widgetclass_t.
 *		The new location can be retrieved by \ref ei_widget_get_screen_location.
 *
 * @param	widget		The widget instance to notify of a geometry change.
 */
void frame_geomnotifyfunc(ei_widget_t widget){
    ei_impl_frame_t *frame = (ei_impl_frame_t *)widget;
    frame->widget.content_rect = frame->widget.screen_location;
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
bool frame_handlefunc(ei_widget_t widget,
                      struct ei_event_t*	event){
    
        /* assert the widget is placed */
    if(widget->placer_params == NULL){return false;}

        // children event
    bool is_consumed = false;
    ei_widget_t current = widget->children_head;
    while (current != NULL && !is_consumed){
        is_consumed = current->wclass->handlefunc(current, event);
        current = current->next_sibling;
    }
    return is_consumed;    
    }

