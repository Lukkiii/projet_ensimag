#include "ei_widget_configure.h"
#include "hw_interface.h"
#include "ei_draw.h"
#include "ei_utils.h"

#include "ei_impl_toplevel_t.h"
#include "ei_impl_button_t.h"
#include "ei_impl_frame_t.h"

void ei_toplevel_configure		(ei_widget_t		widget,
                                ei_size_t*		requested_size,
                                ei_color_t*		color,
                                int*			border_width,
                                ei_string_t*		title,
                                bool*			closable,
                                ei_axis_set_t*		resizable,
                                ei_size_ptr_t*		min_size){

            ei_impl_toplevel_t* toplevel = (ei_impl_toplevel_t*)widget;
            
            if (color != NULL)          {toplevel->color = *color;}
            if (border_width != NULL)   {toplevel->border_width = *border_width;}

            if (title != NULL)               {
                free(toplevel->title);
                if(*title != NULL){
                    toplevel->title = malloc((strlen(*title)+1)*sizeof(char));
                    strcpy(toplevel->title, *title);
                }
                else{
                toplevel->title = NULL;
                }
            }

            if (closable != NULL)       {toplevel->closable = *closable;}
            if (resizable != NULL)      {toplevel->resizable = *resizable;}
            if (min_size != NULL)           {
                free(toplevel->min_size);
                toplevel->min_size = malloc(sizeof(ei_rect_t));
                *(toplevel->min_size) = **min_size;
            }
            if (requested_size != NULL) {toplevel->widget.requested_size = ei_size_add(*requested_size, ei_size(2*toplevel->border_width, 22+toplevel->border_width));}
            }


void ei_frame_configure(ei_widget_t widget,
                        ei_size_t *requested_size,
                        const ei_color_t *color,
                        int *border_width,
                        ei_relief_t *relief,
                        ei_string_t *text,
                        ei_font_t *text_font,
                        ei_color_t *text_color,
                        ei_anchor_t *text_anchor,
                        ei_surface_t *img,
                        ei_rect_ptr_t *img_rect,
                        ei_anchor_t *img_anchor){
    ei_impl_frame_t *frame =  (ei_impl_frame_t*)widget;

    if(requested_size != NULL)  {frame->widget.requested_size = *requested_size;}
    if(border_width != NULL)    {frame->border_width = *border_width;}
    if(color != NULL)           {frame->color = *color;} 
    if(relief != NULL)          {frame->relief = *relief;}

    if (text != NULL)               {
        free(frame->text);
        if(*text != NULL){
            frame->text = malloc((strlen(*text)+1)*sizeof(char));
            strcpy(frame->text, *text);
        }
        else{
            frame->text = NULL;
        }
    }
    if(text_font != NULL)       {frame->text_font = *text_font;}
    if(text_color != NULL)      {frame->text_color = *text_color;}
    if(text_anchor != NULL)     {frame->text_anchor = *text_anchor;}

    if (img != NULL)                {
        if(frame->img != NULL){
            hw_surface_unlock(frame->img);
            hw_surface_free(frame->img);
        }
        hw_surface_lock(*img);
        frame->img = hw_surface_create(*img, hw_surface_get_size(*img), false);
        hw_surface_lock(frame->img);
        ei_copy_surface(frame->img, NULL, *img, NULL, false);
        hw_surface_unlock(*img);
    }
    if (img_rect != NULL)           {
        free(frame->img_rect);
        frame->img_rect = malloc(sizeof(ei_rect_t));
        *(frame->img_rect) = **img_rect;
    }
    if(img_anchor != NULL)      {frame->img_anchor = *img_anchor;}
}



void			ei_button_configure		(ei_widget_t		widget,
							 ei_size_t*		requested_size,
							 const ei_color_t*	color,
							 int*			border_width,
							 int*			corner_radius,
							 ei_relief_t*		relief,
							 ei_string_t*		text,
							 ei_font_t*		text_font,
							 ei_color_t*		text_color,
							 ei_anchor_t*		text_anchor,
							 ei_surface_t*		img,
							 ei_rect_ptr_t*		img_rect,
							 ei_anchor_t*		img_anchor,
							 ei_callback_t*		callback,
							 ei_user_param_t*	user_param){

    ei_impl_button_t* button = (ei_impl_button_t*)widget;
    // if not null                  set value
    if (requested_size != NULL)     {button->widget.requested_size = *requested_size;}
    if (color != NULL)              {button->color = *color;}
    if (border_width != NULL)       {button->border_width = *border_width;}
    if (corner_radius != NULL)      {button->corner_radius = *corner_radius;}
    if (relief != NULL)             {button->relief = *relief;}

    if (text != NULL)               {
        free(button->text);
        if(*text != NULL){
            button->text = malloc((strlen(*text)+1)*sizeof(char));
            strcpy(button->text, *text);
        }
        else{
            button->text = NULL;
        }
    }
    if (text_font != NULL)          {button->text_font = *text_font;}
    if (text_color != NULL)         {button->text_color = *text_color;}
    if (text_anchor != NULL)        {button->text_anchor = *text_anchor;}

    if (img != NULL)                {
        if(button->img != NULL){
            hw_surface_unlock(button->img);
            hw_surface_free(button->img);
        }
        hw_surface_lock(*img);
        button->img = hw_surface_create(*img, hw_surface_get_size(*img), false);
        hw_surface_lock(button->img);
        ei_copy_surface(button->img, NULL, *img, NULL, false);
        hw_surface_unlock(*img);
    }
    if (img_rect != NULL)           {
        free(button->img_rect);
        button->img_rect = malloc(sizeof(ei_rect_t));
        *(button->img_rect) = **img_rect;
    }
    if (img_anchor != NULL)         {button->img_anchor = *img_anchor;}

    if (callback != NULL)           {button->callback = *callback;}
    if (user_param != NULL)         {button->user_param = *user_param;}
}
