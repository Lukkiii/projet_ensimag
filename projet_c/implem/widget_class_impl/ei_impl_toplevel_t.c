#include "ei_impl_toplevel_t.h"
#include "ei_utils.h"
#include "hw_interface.h"
#include "ei_draw.h" 
#include "ei_impl_draw_utils.h"
#include "ei_event.h"
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
ei_widget_t toplevel_allocfunc(){
    ei_impl_toplevel_t *toplevel = calloc(1, sizeof(ei_impl_toplevel_t));
    toplevel->min_size = calloc(1, sizeof(ei_size_t));
    return (ei_widget_t)toplevel;
}

/**
 * \brief	A function that releases the memory used by a widget before it is destroyed.
 *		The memory used for the \ref ei_widget_t structure itself must *not* be freed by
 *		the function. It frees the *fields* of the structure, if necessary.
 *		Can be set to NULL in \ref ei_widgetclass_t if no memory is used by a class of widget.
 *
 * @param	widget		The widget which resources are to be freed.
 */
void toplevel_releasefunc(ei_widget_t widget){
    ei_impl_toplevel_t *toplevel = (ei_impl_toplevel_t *)widget;
    free(toplevel->min_size);
    free(toplevel->title);
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
*				(expressed in the surface reference toplevel).
*/
void toplevel_drawfunc(ei_widget_t widget,
                    ei_surface_t		surface,
                    ei_surface_t		pick_surface,
                    ei_rect_t*		clipper){
    
            /* assert the widget is placed */
        if(widget->placer_params == NULL){return;}
        
        ei_impl_toplevel_t* toplevel = (ei_impl_toplevel_t*)widget;
        ei_rect_t rect = toplevel->widget.screen_location;
        ei_point_t NW = rect.top_left;
        ei_point_t SE = ei_point_add(NW , ei_size_as_point(rect.size));
        ei_point_t NE = ei_point( SE.x,  NW.y);
        ei_point_t SW = ei_point( NW.x,  SE.y);
        ei_point_t pts[5];
        pts[0] = ei_point(NW.x , NW.y +22);
        pts[1] = ei_point(NE.x , NE.y +22);
        pts[2] = SE;
        pts[3] = SW;
        pts[4] = ei_point(NW.x , NW.y +22);
        ei_draw_polygon(surface, pts, 5, toplevel->color, clipper);

        //dessin de l'offscreen interieur 
        ei_point_t pts_off_inter[5];
        pts_off_inter[0] = ei_point(NW.x + toplevel->border_width, NW.y + 22);
        pts_off_inter[1] = ei_point(NE.x - toplevel->border_width, NE.y + 22);
        pts_off_inter[2] = ei_point(SE.x - toplevel->border_width, SE.y - toplevel->border_width);
        pts_off_inter[3] = ei_point(SW.x + toplevel->border_width, SW.y - toplevel->border_width);
        pts_off_inter[4] = ei_point(NW.x + toplevel->border_width, NW.y + 22);
        ei_draw_polygon(pick_surface , pts_off_inter, 5,*(widget->pick_color) , clipper);

        // draw children
            //calcule du clipper pour les fils
        ei_point_t topleft_dessin = ei_point_add(NW, ei_point(toplevel->border_width, 22));
        ei_size_t size_dessin = ei_size(SE.x - NW.x - 2 * toplevel->border_width, SE.y - NW.y - (22 + toplevel->border_width));
        ei_rect_t rect_de_dessin = ei_rect(topleft_dessin, size_dessin);
        ei_rect_t* clipper_child = NULL;
        if(clipper != NULL) {
            ei_rect_t clipper_child_v = ei_intersect_rect(rect_de_dessin, *clipper);
            clipper_child = &clipper_child_v;
        }
        ei_widget_t current = widget->children_tail;
        while (current != NULL){
            current->wclass->drawfunc(current, surface, pick_surface, clipper_child);
            current = current->prev_sibling;
        }

        //bande gauche
        ei_point_t pts_bdg[5];
        pts_bdg[0] = ei_point( NW.x , NW.y + 22);
        pts_bdg[1] = SW;
        pts_bdg[2] = ei_point( SW.x + toplevel->border_width , SW.y );
        pts_bdg[3] = ei_point( NW.x + toplevel->border_width , NW.y + 22 );
        pts_bdg[4] = ei_point( NW.x , NW.y + 22);
        ei_draw_polygon(surface, pts_bdg, 5, (ei_color_t) { 0x44, 0x44, 0x44, 0xff }, clipper);
        ei_draw_polygon(pick_surface , pts_bdg, 5,*(widget->pick_color) , clipper);

        //bande droite
        ei_point_t pts_bdd[5];
        pts_bdd[0] = ei_point( NE.x , NE.y + 22);
        pts_bdd[1] = SE;
        pts_bdd[2] = ei_point( SE.x -toplevel->border_width , SE.y );
        pts_bdd[3] = ei_point( NE.x - toplevel->border_width , NE.y + 22 );
        pts_bdd[4] = ei_point( NE.x , NE.y + 22);
        ei_draw_polygon(surface, pts_bdd, 5, (ei_color_t) { 0x44, 0x44, 0x44, 0xff }, clipper);
        ei_draw_polygon(pick_surface , pts_bdd, 5,*(widget->pick_color) , clipper);

        //bande basse
        ei_point_t pts_bdb[5];
        pts_bdb[0] = SE;
        pts_bdb[1] = SW;
        pts_bdb[2] = ei_point( SW.x , SW.y - toplevel->border_width);
        pts_bdb[3] = ei_point( SE.x , SE.y - toplevel->border_width);
        pts_bdb[4] = SE;
        ei_draw_polygon(surface, pts_bdb, 5, (ei_color_t) { 0x44, 0x44, 0x44, 0xff }, clipper);
        ei_draw_polygon(pick_surface , pts_bdb, 5,*(widget->pick_color) , clipper);

        //bande haute
        ei_rect_t rect_location;
        ei_line_t* bdh;
        ei_size_t size = ei_size( NE.x - NW.x, 22);
        rect_location = ei_rect(NW, size);
        bdh = create_line_smooth_rect_top(rect_location, 11);
        ei_draw_polygon(surface, bdh->points, bdh->length, (ei_color_t) { 0x44, 0x44, 0x44, 0xff }, clipper);
        ei_draw_polygon(pick_surface ,  bdh->points, bdh->length, *(widget->pick_color) , clipper);
        free_line(bdh);
        //bouton de redimentionnement
        ei_point_t pts_br[5];
        pts_br[0] = ei_point( SE.x  , SE.y );
        pts_br[1] = ei_point( SE.x  , SE.y -12);
        pts_br[2] = ei_point( SE.x -12 , SE.y -12);
        pts_br[3] = ei_point( SE.x -12 , SE.y );
        pts_br[4] = ei_point( SE.x  , SE.y );
        ei_draw_polygon(surface, pts_br, 5, (ei_color_t) { 0x44, 0x44, 0x44, 0xff }, clipper);
        ei_draw_polygon(pick_surface , pts_br, 5,*(widget->pick_color) , clipper);

        //bouton de la toplevel
        ei_point_t centre = ei_point(NW.x +20 , NW.y +10);
        ei_point_t pts_cer[19];
        pts_cer[0] = ei_point(centre.x - 3 ,centre.y - 8);
        pts_cer[1] = ei_point(centre.x + 3 ,centre.y - 8);
        pts_cer[2] = ei_point(centre.x + 5 ,centre.y - 7);
        pts_cer[3] = ei_point(centre.x + 7 ,centre.y - 5);
        pts_cer[4] = ei_point(centre.x + 8 , centre.y - 3);
        pts_cer[5] = ei_point(centre.x + 8 , centre.y + 3);
        pts_cer[6] = ei_point(centre.x + 7 , centre.y + 5);
        pts_cer[7] = ei_point(centre.x + 5 , centre.y + 7);
        pts_cer[8] = ei_point(centre.x + 3 , centre.y + 8);
        pts_cer[9] = ei_point(centre.x + 2 , centre.y + 9);
        pts_cer[10] = ei_point(centre.x + 2 , centre.y + 9);
        pts_cer[11] = ei_point(centre.x - 3 , centre.y + 8);
        pts_cer[12] = ei_point(centre.x - 5 , centre.y + 7);
        pts_cer[13] = ei_point(centre.x - 7 , centre.y + 5);
        pts_cer[14] = ei_point(centre.x - 8 , centre.y + 3);
        pts_cer[15] = ei_point(centre.x - 8 , centre.y - 3);
        pts_cer[16] = ei_point(centre.x - 7 ,centre.y - 5);
        pts_cer[17] = ei_point(centre.x - 5 ,centre.y - 7);
        pts_cer[18] = ei_point(centre.x - 3 ,centre.y - 8);
        ei_draw_polygon(surface, pts_cer, 19, (ei_color_t) { 0xA0, 0x00, 0x00, 0xff } , clipper);

        //titre de la toplevel
        if(toplevel->title != NULL && strcmp(toplevel->title, "") != 0){
        ei_size_t text_size = {0,0};
        hw_text_compute_size(toplevel->title, ei_default_font, &text_size.width, &text_size.height);
        ei_point_t anchor = ei_point(NW.x + 34,
                                     NW.y + (20 - text_size.height)/2);
        ei_draw_text(surface, &anchor, toplevel->title, ei_default_font, (ei_color_t){0x0, 0x0, 0x0, 0xff}, clipper);
        
        }
}

/**
 * \brief	A function that sets the default values for a widget of this class.
 *
 * @param	widget		A pointer to tNW.x +20he widget instance to initialize.
 */
void toplevel_setdefaultsfunc(ei_widget_t widget){
        ei_impl_toplevel_t* toplevel = (ei_impl_toplevel_t*)widget;
        toplevel->widget.requested_size = ei_size(320,240);
        toplevel->widget.screen_location = ei_rect(ei_point_zero(),ei_size(600,600));
        toplevel->color = ei_default_background_color;
        toplevel->border_width = 0;
        toplevel->title = malloc(9*sizeof(char));
        strcpy(toplevel->title, "Toplevel");
        toplevel->closable = true;
        toplevel->resizable = ei_axis_both;
        *(toplevel->min_size) = ei_size(160, 120);
        toplevel->state = ei_toplevel_none;
        toplevel->state_origin = (ei_point_t){0,0};
}


/**
 * \brief 	A function that is called to notify the widget that its geometry has been modified
 *		by its geometry manager. Can set to NULL in \ref ei_widgetclass_t.
*		The new location can be retrieved by \ref ei_widget_get_screen_location.
*
* @param	widget		The widget instance to notify of a geometry change.
*/
void toplevel_geomnotifyfunc(ei_widget_t widget){
    
    ei_impl_toplevel_t *toplevel = (ei_impl_toplevel_t *)widget;
    widget->content_rect = widget->screen_location;

    widget->content_rect.top_left.x  += toplevel->border_width;
    widget->content_rect.top_left.y  += 22;
    widget->content_rect.size.width -= 2*toplevel->border_width;
    widget->content_rect.size.height -= toplevel->border_width + 22;
}




/**
 * @brief Set widget in front of widget of parent children list
 * 
 * @param   widget  The widget to place
 * 
 * @param   parent  The parent of the widget
*/

void ei_widget_set_first_child(ei_widget_t widget, ei_widget_t parent){
    // assert it as a parent
    if(parent == NULL || widget == NULL){return;};
    // set widget in front of parent's children list
    if(widget->parent != NULL){
        if(widget->parent->children_head == widget){
            widget->parent->children_head = widget->next_sibling;
        }
        if(widget->parent->children_tail == widget){
            widget->parent->children_tail = widget->prev_sibling;
        }
    }

    if(widget->next_sibling != NULL){
        widget->next_sibling->prev_sibling = widget->prev_sibling;
    }
    if(widget->prev_sibling != NULL){
        widget->prev_sibling->next_sibling = widget->next_sibling;
    }


    widget->parent = parent;
    widget->next_sibling = parent->children_head;
    if(widget->next_sibling != NULL){
        widget->next_sibling->prev_sibling = widget;
    } else {
        parent->children_tail = widget;
    }
    widget->prev_sibling = NULL;
    parent->children_head = widget;
}


/**
 * @brief Check if widget with precised pick_id is a decendant of root
 * 
 * @param   pick_id     the id of the widget to search
 * 
 * @param   root        the root of research 
*/
bool ei_widget_is_in(uint32_t pick_id, ei_widget_t root){
    // root is the widget search
    if(root->pick_id == pick_id){
        return true;
    }
    // check in children
    ei_widget_t current = root->children_head;
    while (current != NULL)
    {
        if(ei_widget_is_in(pick_id, current)){
            return true;
        }
        current = current->next_sibling;
    }
    // not found
    return false;
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
*				false, or return true without having done anything.ei_app_offscreen
*/


bool toplevel_handlefunc(ei_widget_t widget,
                      struct ei_event_t*	event){
    
        /* assert the widget is placed */
    if(widget->placer_params == NULL){return false;}

    ei_impl_toplevel_t* toplevel = (ei_impl_toplevel_t*)widget;

    if(event->type == ei_ev_mouse_buttonup && event->param.mouse.button == ei_mouse_button_left && ei_event_get_active_widget() == widget){
        // is closing
        if(toplevel->state == ei_toplevel_closing){
            uint32_t mouse_id = ei_widget_pick_id_at(&(event->param.mouse.where));
            if(mouse_id == widget->pick_id){
                ei_point_t distance = ei_point_sub(event->param.mouse.where , ei_point_add(widget->screen_location.top_left, ei_point(20, 10)));
                if(distance.x*distance.x + distance.y*distance.y < 81){
                    ei_placer_forget(widget);
                }
            }
        }
        // reset state
        toplevel->state = ei_toplevel_none;
        ei_event_set_active_widget(NULL);
        return true;
    }

    if(event->type == ei_ev_mouse_move && ei_event_get_active_widget() == widget){

        if(toplevel->state == ei_toplevel_moving){
            int new_x = widget->screen_location.top_left.x - toplevel->state_origin.x + event->param.mouse.where.x;
            int new_y = widget->screen_location.top_left.y - toplevel->state_origin.y + event->param.mouse.where.y;
            ei_place(widget, NULL, &new_x, &new_y, NULL, NULL, NULL, NULL, NULL, NULL);
            toplevel->state_origin = event->param.mouse.where;
        }
        if(toplevel->state == ei_toplevel_resizing){
            int new_x = max(widget->screen_location.size.width - toplevel->state_origin.x + event->param.mouse.where.x, toplevel->min_size->width);
            int new_y = max(widget->screen_location.size.height - toplevel->state_origin.y + event->param.mouse.where.y, toplevel->min_size->height);
            int* adnx = NULL;
            int* adny = NULL;
            if(toplevel->resizable == ei_axis_x || toplevel->resizable == ei_axis_both){
                adnx = &new_x;
            }
            if(toplevel->resizable == ei_axis_y || toplevel->resizable == ei_axis_both){
                adny = &new_y;
            }
            ei_place(widget, NULL, NULL, NULL, adnx, adny, NULL, NULL, NULL, NULL);
            toplevel->state_origin = (ei_point_t){new_x + widget->screen_location.top_left.x, new_y + widget->screen_location.top_left.y};
        }

        return true;
    }

    // set toplevel in front
    if(event->type == ei_ev_mouse_buttondown){
        uint32_t mouse_id = ei_widget_pick_id_at(&(event->param.mouse.where));
        if(ei_widget_is_in(mouse_id, widget)){
            ei_widget_set_first_child(widget, widget->parent);
        }
        if(event->param.mouse.button == ei_mouse_button_left && mouse_id == widget->pick_id){
            // closing
            if(toplevel->closable){
                ei_point_t distance = ei_point_sub(event->param.mouse.where , ei_point_add(widget->screen_location.top_left, ei_point(20+toplevel->border_width, 10)));
                if(distance.x*distance.x + distance.y*distance.y < 81){
                    toplevel->state = ei_toplevel_closing;
                    ei_event_set_active_widget(widget);
                    return true;
                }
            }
            // resizing
            if(toplevel->resizable != ei_axis_none){
                // set where to a different origin (bottom right of toplevel)
                int wx_BR = event->param.mouse.where.x - toplevel->widget.screen_location.top_left.x  - toplevel->widget.screen_location.size.width;
                int wy_BR = event->param.mouse.where.y - toplevel->widget.screen_location.top_left.y  - toplevel->widget.screen_location.size.height;
                if(-12 <= wx_BR && wx_BR <= 0 && -12 <= wy_BR && wy_BR <= 0){
                    toplevel->state = ei_toplevel_resizing;
                    toplevel->state_origin = event->param.mouse.where;
                    ei_event_set_active_widget(widget);
                    return true;
                }
            } 
            // moving
                // set where to a different origin (top left of toplevel)
            int wx_TL = event->param.mouse.where.x - toplevel->widget.screen_location.top_left.x;
            int wy_TL = event->param.mouse.where.y - toplevel->widget.screen_location.top_left.y;
            if(-toplevel->border_width <= wx_TL && wx_TL <= toplevel->widget.screen_location.size.width + toplevel->border_width && 0 <= wy_TL && wy_TL <= 22){
                toplevel->state = ei_toplevel_moving;
                toplevel->state_origin = event->param.mouse.where;
                ei_event_set_active_widget(widget);
                return true;
            }
        }
    }     
    
    // children event
    bool is_consumed = false;
    ei_widget_t current = widget->children_head;
    while (current != NULL && !is_consumed){
        is_consumed = current->wclass->handlefunc(current, event);
        current = current->next_sibling;
    }  
    if(event->type == ei_ev_keyup || event->type == ei_ev_keyup){
        return true;
    }
    return is_consumed;
}

