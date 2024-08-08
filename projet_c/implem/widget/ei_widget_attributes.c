#include "ei_widget_attributes.h"
#include "ei_implementation.h"

/**
 * @brief	Get the class of a widget.
 *
 * @param	widget		The widget.
 *
 * @return			The structure that describe the class of this widget.
 */
ei_widgetclass_t*	ei_widget_get_class		(ei_widget_t		widget){
    return widget->wclass;
}

/**
 * @brief	Get the identifier of the widget in the picking offscreen under the form
 * 		of a color.
 *
 * @param	widget		The widget.
 *
 * @return			The (false)color of the pixels that represent this widget
 * 				in the picking offscreen.
 */
const ei_color_t*	ei_widget_get_pick_color	(ei_widget_t		widget){
    return widget->pick_color;
}

/**
 * @brief	Get the parent of a widget.
 *
 * @param	widget		The widget.
 *
 * @return			The parent of the widget, or NULL if called on the root widget.
 */
ei_widget_t 		ei_widget_get_parent		(ei_widget_t		widget){
    return widget->parent;
}

/**
 * @brief	Get the first child of a widget.
 *
 * @param	widget		The widget.
 *
 * @return			The first child, or NULL if the widget has no child.
 */
ei_widget_t 		ei_widget_get_first_child	(ei_widget_t		widget){
    return widget->children_head;
}

/**
 * @brief	Get the last child of a widget.
 *
 * @param	widget		The widget.
 *
 * @return			The last child, or NULL if the widget has no child.
 */
ei_widget_t 		ei_widget_get_last_child	(ei_widget_t		widget){
    return widget->children_tail;
}

/**
 * @brief	Get the next sibling of a widget.
 *
 * @param	widget		The widget.
 *
 * @return			The sibling next to this widget in the widget's parent sibling order.
 * 				NULL if the widget is the last child or the root widget.
 */
ei_widget_t ei_widget_get_next_sibling (ei_widget_t widget){
	return widget->next_sibling;
}

/**
 * @brief	Get the user data associated to a widget.
 *
 * @param	widget		The widget.
 *
 * @return			The user data associated with the widget.
 */
void* ei_widget_get_user_data (ei_widget_t widget){
	return widget->user_data;
}

/**
 * @brief	Get the screen location of the widget.
 *
 * @param	widget		The widget.
 *
 * @return			The location of the widget on the screen, expressed in the screen reference
 * 				(origin at the top/left of the screen, ordinates increasing downward).
 */
const ei_rect_t*	ei_widget_get_screen_location	(ei_widget_t		widget){
    return &widget->screen_location;
}

/**
 * @brief	Get the content rect of a widget.
 *
 * @param	widget		The widget.
 *
 * @return			The rectangle, expressed in screen coordinates, that defines where the children can be drawn.
 */
const ei_rect_t*	ei_widget_get_content_rect	(ei_widget_t		widget){
    return &widget->content_rect;
}

/**
 * @brief	Set the content rect of a widget.
 *
 * @param	widget		The widget.
 * @param	content_rect	The rectangle, expressed in screen coordinates, that defines where the children can be drawn.
 */
void	 		ei_widget_set_content_rect	(ei_widget_t		widget,
							                const ei_rect_t*	content_rect){
    widget->content_rect = *content_rect;
}





