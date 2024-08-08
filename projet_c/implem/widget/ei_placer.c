#include "ei_placer.h"
#include "ei_implementation.h"
#include "ei_utils.h"




ei_point_t ei_widget_anchor_to_abs_point(ei_anchor_t* anchor_ptr, ei_point_t anchor_point, ei_size_t widget_size){
	ei_point_t position = anchor_point;
	ei_anchor_t anchor = ei_anc_none;
	if(anchor_ptr != NULL){
		anchor = *anchor_ptr;
	}
	// x
	switch (anchor)
	{
		// centered
		case ei_anc_north:	
		case ei_anc_center:		
		case ei_anc_south:	
			position.x -= (widget_size.width+1)/2;
			break;
		// east
		case ei_anc_northeast:		
		case ei_anc_east:		
		case ei_anc_southeast:	
			position.x -= widget_size.width;
			break;
		// west
		default:
			break;
	}
	// y
	switch (anchor)
	{
		// centered
		case ei_anc_west:
		case ei_anc_center:		
		case ei_anc_east:
			position.y -= (widget_size.height+1)/2;
			break;
		// south
		case ei_anc_southwest:
		case ei_anc_south:
		case ei_anc_southeast:	
			position.y -= widget_size.height;	
		// north
		default:
			break;
	}
	return position;
}


/**
 * \brief	Configures the geometry of a widget using the "placer" geometry manager.
 *
 *		The placer computes a widget's geometry relative to its parent *content_rect*.
 *
 * 		If the widget was already managed by the "placer", then this calls simply updates
 *		the placer parameters: arguments that are not NULL replace previous values.
 *
 * 		When the arguments are passed as NULL, the placer uses default values (detailed in
 *		the argument descriptions below). If no size is provided (either absolute or
 *		relative), then either the requested size of the widget is used if one was provided,
 *		or the default size is used.
 *
 * @param	widget		The widget to place.
 * @param	anchor		How to anchor the widget to the position defined by the placer
 *				(defaults to ei_anc_northwest).
 * @param	x		The absolute x position of the widget (defaults to 0).
 * @param	y		The absolute y position of the widget (defaults to 0).
 * @param	width		The absolute width for the widget (defaults to the requested width or
 * 				the default width of the widget if rel_width is NULL, or 0 otherwise).
 * @param	height		The absolute height for the widget (defaults to the requested height or
 *				the default height of the widget if rel_height is NULL, or 0 otherwise).
 * @param	rel_x		The relative x position of the widget: 0.0 corresponds to the left
 *				side of the master, 1.0 to the right side (defaults to 0.0).
 * @param	rel_y		The relative y position of the widget: 0.0 corresponds to the top
 *				side of the master, 1.0 to the bottom side (defaults to 0.0).
 * @param	rel_width	The relative width of the widget: 0.0 corresponds to a width of 0,
 *				1.0 to the width of the master (defaults to 0.0).
 * @param	rel_height	The relative height of the widget: 0.0 corresponds to a height of 0,
 *				1.0 to the height of the master (defaults to 0.0).
 */
void ei_place	(ei_widget_t		widget,
				 ei_anchor_t*		anchor,
				 int*			x,
				 int*			y,
				 int*			width,
				 int*			height,
				 float*			rel_x,
				 float*			rel_y,
				 float*			rel_width,
				 float*			rel_height){

		/* allocate memory if not already placed */
		if(widget->placer_params == NULL){
			widget->placer_params = malloc(sizeof(ei_impl_placer_params_t));
			widget->placer_params->anchor = NULL;
			widget->placer_params->x = NULL;
			widget->placer_params->y = NULL;
			widget->placer_params->width = NULL;
			widget->placer_params->height = NULL;
			widget->placer_params->rel_x = NULL;
			widget->placer_params->rel_y = NULL;
			widget->placer_params->rel_width = NULL;
			widget->placer_params->rel_height = NULL;
		}

		/* widget update parameters */
		//anchor
		if(anchor != NULL){
			if(widget->placer_params->anchor == NULL){
				widget->placer_params->anchor = malloc(sizeof(ei_anchor_t));
			}
			*(widget->placer_params->anchor) = *anchor;
		}
		//x
		if(x != NULL){
			if(widget->placer_params->x == NULL){
				widget->placer_params->x = malloc(sizeof(int));
				}
			*(widget->placer_params->x) = *x;
		}
		//y
		if(y != NULL){
			if(widget->placer_params->y == NULL){
				widget->placer_params->y = malloc(sizeof(int));
				}
			*(widget->placer_params->y) = *y;
		}
		//width
		if(width != NULL){
			if(widget->placer_params->width == NULL){
				widget->placer_params->width = malloc(sizeof(int));
				}
			*(widget->placer_params->width) = *width;
		}
		//heigth
		if(height != NULL){
			if(widget->placer_params->height == NULL){
				widget->placer_params->height = malloc(sizeof(int));
				}
			*(widget->placer_params->height) = *height;
		}
		//rel x
		if(rel_x != NULL){
			if(widget->placer_params->rel_x == NULL){
				widget->placer_params->rel_x = malloc(sizeof(float));
				}
			*(widget->placer_params->rel_x) = *rel_x;
		}
		//rel y
		if(rel_y != NULL){
			if(widget->placer_params->rel_y == NULL){
				widget->placer_params->rel_y = malloc(sizeof(float));
				}
			*(widget->placer_params->rel_y) = *rel_y;
		}
		//rel width
		if(rel_width != NULL){
			if(widget->placer_params->rel_width == NULL){
				widget->placer_params->rel_width = malloc(sizeof(float));
				}
			*(widget->placer_params->rel_width) = *rel_width;
		}
		//rel height
		if(rel_height != NULL){
			if(widget->placer_params->rel_height == NULL){
				widget->placer_params->rel_height = malloc(sizeof(float));
				}
			*(widget->placer_params->rel_height) = *rel_height;
		}


		/* calculate width */
		if(widget->placer_params->width != NULL){
			widget->screen_location.size.width = *widget->placer_params->width;
		} else if(widget->placer_params->rel_width != NULL && widget->parent != NULL){
			widget->screen_location.size.width = (int)((*widget->placer_params->rel_width) * widget->parent->content_rect.size.width);
		} else {
			widget->screen_location.size.width = widget->requested_size.width;
		}

		/* calculate height */
		if(widget->placer_params->height != NULL){
			widget->screen_location.size.height = *widget->placer_params->height;
		} else if(widget->placer_params->rel_height != NULL && widget->parent != NULL){
			widget->screen_location.size.height = (int)((*widget->placer_params->rel_height) * widget->parent->content_rect.size.height);
		} else {
			widget->screen_location.size.height = widget->requested_size.height;
		}

		/* calculate coord */
		int anchor_x = 0;
		if(widget->parent != NULL){
			anchor_x += widget->parent->content_rect.top_left.x;
		}
		if(widget->placer_params->rel_x != NULL){
			anchor_x += (int)(*(widget->placer_params->rel_x) * widget->parent->content_rect.size.width);
		}
		if(widget->placer_params->x != NULL){
			anchor_x += *(widget->placer_params->x);
		}

		
		int anchor_y = 0;
		if(widget->parent != NULL){
			anchor_y += widget->parent->content_rect.top_left.y;
		}
		if(widget->placer_params->rel_y != NULL){
			anchor_y += (int)(*(widget->placer_params->rel_y) * widget->parent->content_rect.size.height);
		}
		if(widget->placer_params->y != NULL){
			anchor_y += *(widget->placer_params->y);
		}
		
		widget->screen_location.top_left = ei_widget_anchor_to_abs_point(widget->placer_params->anchor, (ei_point_t){anchor_x, anchor_y}, widget->screen_location.size);
		widget->wclass->geomnotifyfunc(widget);
		
		/*placer les fils*/
		ei_widget_t current = widget->children_head;
		while (current != NULL){
			if(current->placer_params != NULL){
				ei_place(current ,NULL ,NULL ,NULL ,NULL ,NULL ,NULL ,NULL ,NULL ,NULL);
			}
			current = current->next_sibling;
    	}
	}


/**
 * \brief	Tells the placer to remove a widget from the screen and forget about it.
 *		Note: the widget is not destroyed and still exists in memory.
 *
 * @param	widget		The widget to remove from screen.
 */
void		ei_placer_forget(ei_widget_t widget){
	/* free last value */
	if(widget->placer_params != NULL){
		free(widget->placer_params->anchor);
		free(widget->placer_params->x);
		free(widget->placer_params->y);
		free(widget->placer_params->width);
		free(widget->placer_params->height);
		free(widget->placer_params->rel_x);
		free(widget->placer_params->rel_y);
		free(widget->placer_params->rel_width);
		free(widget->placer_params->rel_height);
		/* free */
		free(widget->placer_params);
		widget->placer_params = NULL;
	}

}