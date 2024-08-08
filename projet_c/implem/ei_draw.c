#include <stdint.h>
#include <math.h>

#include "ei_draw.h"
#include "ei_impl_draw_utils.h"
#include "ei_types.h"
#include "hw_interface.h"
#include "ei_utils.h"


/**
 * \brief	Draws a line that can be made of many line segments.
 *
 * @param	surface 	Where to draw the line. The surface must be *locked* by
 *				\ref hw_surface_lock.
 * @param	point_array 	The array of points defining the polyline. Its size is provided in the
 * 				next parameter (point_array_size). The array can be empty (i.e. nothing
 * 				is drawn) or it can have a single point, or more.
 *				If the last point is the same as the first point, then this pixel is
 *				drawn only once.
 * @param	point_array_size The number of points in the point_array. Can be 0.
 * @param	color		The color used to draw the line. The alpha channel is managed.
 * @param	clipper		If not NULL, the drawing is restricted within this rectangle.
 */
void ei_draw_polyline(ei_surface_t surface, ei_point_t* point_array, size_t point_array_size, ei_color_t color, const ei_rect_t* clipper){
	
	uint8_t *pixel = hw_surface_get_buffer(surface);
	ei_size_t surface_size = hw_surface_get_size(surface);
	ei_color_t newcolor;
	// printf("%ld\n", point_array_size);

	int min_x = 0; 
	int min_y = 0;
	int max_x = surface_size.width;
	int max_y = surface_size.height;
	if(clipper != NULL){
		min_x = clipper->top_left.x;
		min_y = clipper->top_left.y;
		max_x = clipper->top_left.x+clipper->size.width;
		max_y = clipper->top_left.y+clipper->size.height;
	}
	// printf("min_x : %d\t min_y : %d\t max_x : %d\t max_y : %d\n", min_x, min_y, max_x, max_y);

	for (size_t i = 0; i < point_array_size-1; i++){
		ei_point_t point1 = point_array[i];
		ei_point_t point2 = point_array[i+1];
		
		// printf("point1.x :%d\n", point1.x);
		// printf("point1.y :%d\n", point1.y);
		// printf("point2.x :%d\n", point2.x);
		// printf("point2.y :%d\n", point2.y);
		int x0 = point1.x;
		int x1 = point2.x;
		int y0 = point1.y;
		int y1 = point2.y;
		bool point_exchange = false;

		//|pent|>1
		if (abs(point2.y - point1.y) > abs(point2.x - point1.x))
		{
			x0 = point1.y;
			x1 = point2.y;
			y0 = point1.x;
			y1 = point2.x;
			point_exchange = true;
			// printf("point change!\n");
		}
		
		//if x0 > x1 we exchange these two points
		if(x0 > x1){
			int x_tmp = x0;
			int y_tmp = y0;
			x0 = x1;
			x1 = x_tmp;
			y0 = y1;
			y1 = y_tmp;
		}
		// printf("x0 :%d\n", x0);
		// printf("y0 :%d\n", y0);
		// printf("x1 :%d\n", x1);
		// printf("y1 :%d\n", y1);

		int delta_x = abs(x1 - x0);
		int delta_y = abs(y1 - y0);
		int e = 0;

		int y_step;
		if (y0 <= y1){
			y_step = 1;
		}else{	
			y_step = -1;
		}

		//draw pixels
		uint32_t *pixel_color;
 		
		while(x0 < x1){
			if (!point_exchange){
				if (x0 >= min_x && x0 <= max_x && y0 >= min_y && y0 <= max_y){
					pixel_color = (uint32_t *)(pixel + 4 * x0 + surface_size.width * y0 * 4);
					//printf("x0 = %d\t y0 = %d\n", x0,y0);

					ei_color_t color_source = ei_unmap_rgba(surface, *pixel_color);

					if (!hw_surface_has_alpha(surface)){
						newcolor.red = color.red;
						newcolor.green = color.green;
						newcolor.blue = color.blue;
						*pixel_color = ei_map_rgba(surface, newcolor);
					}else{
						newcolor.alpha = color.alpha;
						newcolor.red = (color.alpha * color.red + (255 - color.alpha) * color_source.red) / 255;
						newcolor.green = (color.alpha * color.green + (255 - color.alpha) * color_source.green) / 255;
						newcolor.blue = (color.alpha * color.blue + (255 - color.alpha) * color_source.blue) / 255;
						*pixel_color = ei_map_rgba(surface, newcolor);
					}
				}		
			}else{
				if (y0 >= min_x && y0 <= max_x && x0 >= min_y && x0 <= max_y){

					pixel_color = (uint32_t *)(pixel + 4 * y0 + surface_size.width * x0 * 4);
					//printf("x0 = %d\t y0 = %d\n", x0, y0);

					ei_color_t color_source = ei_unmap_rgba(surface, *pixel_color);

					if (!hw_surface_has_alpha(surface)){
						newcolor.red = color.red;
						newcolor.green = color.green;
						newcolor.blue = color.blue;
						*pixel_color = ei_map_rgba(surface, newcolor);
					}else{
						newcolor.alpha = color.alpha;
						newcolor.red = (color.alpha * color.red + (255 - color.alpha) * color_source.red) / 255;
						newcolor.green = (color.alpha * color.green + (255 - color.alpha) * color_source.green) / 255;
						newcolor.blue = (color.alpha * color.blue + (255 - color.alpha) * color_source.blue) / 255;
						*pixel_color = ei_map_rgba(surface, newcolor);
					}
				}
			}
			
			x0 += 1;
			e += delta_y;
			if (2*e > delta_x){
				y0 += y_step;
			  	e -= delta_x;
			}	
		}
	}
}





struct cote{
    int ymax;
    int xymin;
	int e;
	int delta_x;
	int delta_y;
    struct cote* suivant ;
};

/**
 * \brief	Draws a filled polygon.
 *
 * @param	surface 	Where to draw the polygon. The surface must be *locked* by
 *				\ref hw_surface_lock.
 * @param	point_array 	The array of points defining the polygon. Its size is provided in the
 * 				next parameter (point_array_size). The array can be empty (i.e. nothing
 * 				is drawn) or else it must have more than 2 points.
 * @param	point_array_size The number of points in the point_array. Must be 0 or more than 2.
 * @param	color		The color used to draw the polygon. The alpha channel is managed.
 * @param	clipper		If not NULL, the drawing is restricted within this rectangle.
 */
void	ei_draw_polygon		(ei_surface_t		surface,
				 ei_point_t*		point_array,
				 size_t			point_array_size,
				 ei_color_t		color,
				 const ei_rect_t*	clipper){

	/* nothing to draw */
	if(point_array_size < 2){return;}

	/* get surface information */
	uint8_t *pixel = hw_surface_get_buffer(surface);
	ei_size_t surface_size = hw_surface_get_size(surface);

	/* set default value if needed */
    if(clipper == NULL){ 
		ei_rect_t new = ei_rect(ei_point_zero(), surface_size);
		clipper = &new;
	}

    /* calculate rect to draw in */
    ei_rect_t draw_rect = ei_intersect_rect(ei_rect(ei_point_zero(), surface_size), *clipper);

    /* get color channel */
    int ir, ig, ib, ia = 0;
	hw_surface_get_channel_indices(surface, &ir, &ig, &ib, &ia);
	set_to_channel_not_used(ir, ig, ib, &ia);

    /*calcule de ymin et ymax dans la liste des points*/
    int xmin = point_array[0].x;
	int ymin = point_array[0].y;
    int xmax = point_array[0].x;
	int ymax = point_array[0].y;
    for(size_t i = 0; i < point_array_size; i++){
        if (point_array[i].x < xmin){
            xmin = point_array[i].x;
        }
        if (point_array[i].y < ymin){
            ymin = point_array[i].y;
        }
        if (point_array[i].x > xmax){
            xmax = point_array[i].x;
        }
        if (point_array[i].y > ymax){
            ymax = point_array[i].y;
        }
    }

    ei_point_t top_left = ei_point(xmin ,ymin);
    ei_point_t bottom_right = ei_point(xmax, ymax);

	size_t TC_length = bottom_right.y - top_left.y + 1;
    struct cote** TC = malloc(TC_length * sizeof(struct cote*));
	for (size_t i = 0; i < TC_length; i++){
		TC[i] = NULL;
	}

	/* TC construction */
    for (size_t i = 0 ; i < point_array_size-1; i++){
		/*construction du coté*/
		if (point_array[i].y != point_array[i+1].y){
			struct cote* cote = malloc(sizeof(struct cote));
			int fct_hachage;
			cote->e = 0;
			if (point_array[i].y < point_array[i+1].y){
				fct_hachage = point_array[i].y;
				cote->xymin = point_array[i].x;
				cote->ymax = point_array[i+1].y;
				cote->delta_y = point_array[i+1].y - point_array[i].y;
				cote->delta_x = point_array[i+1].x - point_array[i].x;
			} else {
				fct_hachage = point_array[i+1].y;
				cote->xymin = point_array[i+1].x;
				cote->ymax = point_array[i].y;
				cote->delta_y = point_array[i].y - point_array[i+1].y;
				cote->delta_x = point_array[i].x - point_array[i+1].x;
			}

			/*fin de construction du coté*/
			cote->suivant = TC[fct_hachage - top_left.y];
			TC[fct_hachage - top_left.y] = cote;
		}
	}


	/* fin TC construction */

	struct cote* TCA = NULL;
    int y = top_left.y;
	struct cote** link;

	struct cote* cote_courant;
	struct cote* cote_precedent;
	struct cote* cote_courant_trie;
	struct cote* cote_suivant;

    while((y - top_left.y) < ((int)TC_length)){
        /* Deplace TC(y) dans TCA */
        /* fin de TC(y) */
        link = &TC[y - top_left.y];
        while(*link!=NULL){
            link = &((**link).suivant);
        }
        *link = TCA;
        TCA = TC[y - top_left.y];
		TC[y - top_left.y] = NULL;

        /* supprimer les y_max dans TCA*/
        link = &TCA;
        cote_courant = TCA;
        while(cote_courant != NULL){
            if(cote_courant->ymax == y){
                *link = cote_courant->suivant;
                free(cote_courant);
                cote_courant = *link; 
            } else {
                link = &cote_courant->suivant;
                cote_courant = cote_courant->suivant;
            }
        }

        /* inverser TCA pour accelerer le tri */
        if(TCA != NULL && TCA->suivant != NULL){
            cote_precedent = TCA;
            cote_courant = cote_precedent->suivant;
			cote_precedent->suivant = NULL;
            while (cote_courant!=NULL)
            {
                cote_suivant = cote_courant->suivant;
                cote_courant->suivant = cote_precedent;
                cote_precedent = cote_courant;
                cote_courant = cote_suivant;
            }
			TCA = cote_precedent;
        }

        /* trier TCA par x_min par insertion */
        if(TCA != NULL){
            cote_courant = TCA->suivant;
            TCA->suivant = NULL;
            while (cote_courant != NULL)
            {
                /* tmp */
                cote_suivant = cote_courant->suivant;
                cote_courant->suivant = NULL;
                
                /* insertion */
				link = &TCA;
                cote_courant_trie = TCA;
                while (cote_courant_trie != NULL && cote_courant->xymin + cote_courant->e/cote_courant->delta_y > cote_courant_trie->xymin + cote_courant_trie->e/cote_courant_trie->delta_y)
                {
					link = &cote_courant_trie->suivant;
                    cote_courant_trie = cote_courant_trie->suivant;
                }
                cote_courant->suivant = cote_courant_trie;
                *link = cote_courant;

                /* couveau cote a inserer */
                cote_courant = cote_suivant;
            }
        }

        /* modifier pixel image */
        if((draw_rect.top_left.y <= y) && (y < draw_rect.top_left.y + draw_rect.size.height)){
            cote_courant = TCA;
            while(cote_courant!=NULL && cote_courant->suivant!=NULL){
                /* dessin entre 2 lignes */
                int x_debut_line = max(cote_courant->xymin + cote_courant->e/cote_courant->delta_y, draw_rect.top_left.x);
                int x_end_line = min(cote_courant->suivant->xymin + cote_courant->suivant->e/cote_courant->suivant->delta_y + 1, draw_rect.top_left.x + draw_rect.size.width);
                for(int x = x_debut_line; x < x_end_line; x++){
                    pixel[4 * surface_size.width * y + 4 * x + ir] = color.red;
				    pixel[4 * surface_size.width * y + 4 * x + ig] = color.green;
				    pixel[4 * surface_size.width * y + 4 * x + ib] = color.blue;
                    pixel[4 * surface_size.width * y + 4 * x + ia] = color.alpha;
                }
                cote_courant = cote_courant->suivant->suivant;
            }
        }

        /* incrementer y */
        y++;

        /* mettre a jour les abscisses de TCA */
        cote_courant = TCA;
        while (cote_courant != NULL)
        {
			cote_courant->e += cote_courant->delta_x;
            cote_courant = cote_courant->suivant;
        }
    }

	free(TC);
}



/**
 * \brief	Fills the surface with the specified color.
 *
 * @param	surface		The surface to be filled. The surface must be *locked* by
 *				\ref hw_surface_lock.
 * @param	color		The color used to fill the surface. If NULL, it means that the
 *				caller want it painted black (opaque).
 * @param	clipper		If not NULL, the drawing is restricted within this rectangle.
 */
void	ei_fill(ei_surface_t		surface,
				const ei_color_t*	color,
				const ei_rect_t*	clipper){
	
	/* get surface information */
	uint8_t *pixel = hw_surface_get_buffer(surface);
	ei_size_t surface_size = hw_surface_get_size(surface);

	/* set default value if needed */
	if(color == NULL){color = &((ei_color_t){ 0x00, 0x00, 0x00, 0xff });}
	if(clipper == NULL){ 
		ei_rect_t new = ei_rect(ei_point_zero(), surface_size);
		clipper = &new;
	}

	/* calculate rect to fill */
	int x_top_left = max(0, clipper->top_left.x);
	int y_top_left = max(0, clipper->top_left.y);
	int x_bottom_right = min(surface_size.width, clipper->top_left.x + clipper->size.width);
	int y_bottom_right = min(surface_size.height, clipper->top_left.y + clipper->size.height);
	
	/* get color address indices */
	int ir, ig, ib, ia = 0;
	hw_surface_get_channel_indices(surface, &ir, &ig, &ib, &ia);
	set_to_channel_not_used(ir, ig, ib, &ia);

	/* fill the rect */
	for(int x = x_top_left; x < x_bottom_right; x++){
		for(int y = y_top_left; y < y_bottom_right; y++){
			pixel[4 * surface_size.width * y + 4 * x + ir] = color->red;
			pixel[4 * surface_size.width * y + 4 * x + ig] = color->green;
			pixel[4 * surface_size.width * y + 4 * x + ib] = color->blue;
			pixel[4 * surface_size.width * y + 4 * x + ia] = color->alpha;
		}
	}
}


/**
 * \brief	Copies pixels from a source surface to a destination surface.
 *		The source and destination areas of the copy (either the entire surfaces, or
 *		subparts) must have the same size before considering clipping.
 *		Both surfaces must be *locked* by \ref hw_surface_lock.
 *
 * @param	destination	The surface on which to copy pixels.
 * @param	dst_rect	If NULL, the entire destination surface is used. If not NULL,
 *				defines the rectangle on the destination surface where to copy
 *				the pixels.
 * @param	source		The surface from which to copy pixels.
 * @param	src_rect	If NULL, the entire source surface is used. If not NULL, defines the
 *				rectangle on the source surface from which to copy the pixels.
 * @param	alpha		If true, the final pixels are a combination of source and
 *				destination pixels weighted by the source alpha channel and
 *				the transparency of the final pixels is set to opaque.
 *				If false, the final pixels are an exact copy of the source pixels,
 				including the alpha channel.
 *
 * @return			Returns 0 on success, 1 on failure (different sizes between
 * 				source and destination).
 */
int	ei_copy_surface	(ei_surface_t		destination,
					 const ei_rect_t*	dst_rect,
					 ei_surface_t		source,
					 const ei_rect_t*	src_rect,
					 bool				alpha){

	/* get surfaces information */
	uint8_t* dst_pixel = hw_surface_get_buffer(destination);
	ei_size_t dst_size = hw_surface_get_size(destination);

	uint8_t* src_pixel = hw_surface_get_buffer(source);
	ei_size_t src_size = hw_surface_get_size(source);

	/* set default value if needed */
	if(dst_rect == NULL){ 
		ei_rect_t new = ei_rect(ei_point_zero(), dst_size);
		dst_rect = &new;
	}
	if(src_rect == NULL){ 
		ei_rect_t new = ei_rect(ei_point_zero(), src_size);
		src_rect = &new;
	}

	/* calulate rect to copy and fill*/
	ei_rect_t dst_to_fill = ei_intersect_rect(*dst_rect, ei_rect(ei_point_zero(), dst_size));
	ei_rect_t src_to_copy = ei_intersect_rect(*src_rect, ei_rect(ei_point_zero(), src_size));

	/* assert the surface are the same size */
	if((dst_to_fill.size.width  != src_to_copy.size.width ) 
	|| (dst_to_fill.size.height != src_to_copy.size.height))
		{return 1;}

	/* get RGBA channels */
	int dir, dig, dib, dia = 0;
	hw_surface_get_channel_indices(destination, &dir, &dig, &dib, &dia);
	set_to_channel_not_used(dir, dig, dib, &dia);
	int sir, sig, sib, sia = 0;
	hw_surface_get_channel_indices(source, &sir, &sig, &sib, &sia);

	/* copy surface */
	if(alpha && sia!=-1) // alpha
	{
		for(int x = 0; x < dst_to_fill.size.width; x++){
			for(int y = 0; y < dst_to_fill.size.height; y++){
				/* addresse du pixel a changer */
				uint8_t* dst_color = &dst_pixel[4 * dst_size.width * (y + dst_to_fill.top_left.y) + 4 * (x + dst_to_fill.top_left.x)];
				uint8_t* src_color = &src_pixel[4 * src_size.width * (y + src_to_copy.top_left.y) + 4 * (x + src_to_copy.top_left.x)];
				/* change de couleur par alpha de la source */
				dst_color[dir] = (uint8_t)((uint16_t)src_color[sia]*(uint16_t)src_color[sir] + ((255 - (uint16_t)src_color[sia])*(uint16_t)dst_color[dir])/255);
				dst_color[dig] = (uint8_t)((uint16_t)src_color[sia]*(uint16_t)src_color[sig] + ((255 - (uint16_t)src_color[sia])*(uint16_t)dst_color[dig])/255);
				dst_color[dib] = (uint8_t)((uint16_t)src_color[sia]*(uint16_t)src_color[sib] + ((255 - (uint16_t)src_color[sia])*(uint16_t)dst_color[dib])/255);
				dst_color[dia] = 255;
			}
		}
	} else if (alpha) { // no alpha
		for(int x = 0; x < dst_to_fill.size.width; x++){
			for(int y = 0; y < dst_to_fill.size.height; y++){
				/* addresse du pixel a changer */
				uint8_t* dst_color = &dst_pixel[4 * dst_size.width * (y + dst_to_fill.top_left.y) + 4 * (x + dst_to_fill.top_left.x)];
				uint8_t* src_color = &src_pixel[4 * src_size.width * (y + src_to_copy.top_left.y) + 4 * (x + src_to_copy.top_left.x)];
				/* change de couleur par alpha de la source */
				dst_color[dir] = src_color[sir];
				dst_color[dig] = src_color[sig];
				dst_color[dib] = src_color[sib];
				dst_color[dia] = 255;	
			}
		}
	} else {
		for(int x = 0; x < dst_to_fill.size.width; x++){
			for(int y = 0; y < dst_to_fill.size.height; y++){
				/* addresse du pixel a changer */
				uint8_t* dst_color = &dst_pixel[4 * dst_size.width * (y + dst_to_fill.top_left.y) + 4 * (x + dst_to_fill.top_left.x)];
				uint8_t* src_color = &src_pixel[4 * src_size.width * (y + src_to_copy.top_left.y) + 4 * (x + src_to_copy.top_left.x)];
				/* change de couleur par alpha de la source */
				dst_color[dir] = src_color[sir];
				dst_color[dig] = src_color[sig];
				dst_color[dib] = src_color[sib];
				dst_color[dia] = src_color[sia];
			}
		}
	}
	
	/* exit success */
	return 0;
}


/**
 * \brief	Draws text by calling \ref hw_text_create_surface.
 *
 * @param	surface 	Where to draw the text. The surface must be *locked* by
 *				\ref hw_surface_lock.
 * @param	where		Coordinates, in the surface, where to anchor the top-left corner of
 *				the rendered text.
 * @param	text		The string of the text. Can't be NULL.
 * @param	font		The font used to render the text. If NULL, the \ref ei_default_font
 *				is used.
 * @param	color		The text color. Can't be NULL. The alpha parameter is not used.
 * @param	clipper		If not NULL, the drawing is restricted within this rectangle.
 */
void	ei_draw_text(	ei_surface_t		surface,
				 		const ei_point_t*	where,
						ei_const_string_t	text,
				 		ei_font_t			font,
				 		ei_color_t			color,
						const ei_rect_t*	clipper){
	/* set default value */
	if(clipper == NULL){ 
		ei_rect_t new = ei_rect(ei_point_zero(), hw_surface_get_size(surface));
		clipper = &new;
	}

	/* create text */
	ei_surface_t text_surface = hw_text_create_surface(text, font, color);
	ei_size_t text_size = hw_surface_get_size(text_surface);
	/* lock text surface */
	hw_surface_lock(text_surface);
	/* copy text */
	ei_rect_t draw_rect = ei_intersect_rect( ei_rect(*where, text_size), *clipper);
	ei_rect_t place = ei_intersect_rect(ei_rect(*where, text_size), draw_rect);
	ei_rect_t text_place = ei_intersect_rect(	ei_rect(ei_point_zero(), text_size), 
												ei_rect(ei_point_sub(draw_rect.top_left,*where), draw_rect.size));
	ei_copy_surface(surface, &place, text_surface, &text_place, true);
	/* unlock text surface */
	hw_surface_unlock(text_surface);
	/* destroy text */
	hw_surface_free(text_surface);
}





