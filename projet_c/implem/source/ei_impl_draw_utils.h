#include "ei_utils.h"
#include "ei_types.h"


/**
 * @brief the min between int a and b
*/
int min(int a, int b);

/**
 * @brief the max between int a and b
*/
int max(int a, int b);

/**
 * @brief Set a to channel not used by rgb componnent
 */
void set_to_channel_not_used(int r, int g, int b, int *a);


/**
 * @brief define a line made of segments (point by point)
 * a polygon is a line which end where it started
*/
typedef struct ei_line_t
{
    int length;
    ei_point_t* points;
}ei_line_t;


/**
 * \brief Return a list of point discribing
 * the quarter of a circle (x and y positive).
 * First point is {size, 0} point.
 *
 * @param   radius      The radius of said circle.
 */
ei_line_t* create_line_arc(int radius, int pas);


/**
 * \brief Retrun a list of point discribing a smooth rectangle
 * as a polygon (for draw polygon),
 * return NULL if it can't be made from parameter
 * 
 * @param   top_left    pointer to top_left corner of rectangle (as if it was not smoothed)
 * 
 * @param   rectangle    pointer to the rectangle to smooth
*/
ei_line_t* create_line_smooth_rect(ei_rect_t rectangle, int corner_radius);


/**
 * \brief Retrun a list of point discribing a smooth rectangle in the top
 * as a polygon (for draw polygon),
 * return NULL if it can't be made from parameter
 * 
 * @param   top_left    pointer to top_left corner of rectangle (as if it was not smoothed)
 * 
 * @param   rectangle    pointer to the rectangle to smooth
*/
ei_line_t* create_line_smooth_rect_top(ei_rect_t rectangle, int corner_radius);


/**
 * \brief Free a line
 * 
 * @param   line     The line to free
*/
void free_line(ei_line_t* line);

/**
 * \brief Rotate the representation of the polygon (line with last = first) by step.
 * 
 * @param   polygon   The polygon to rotate.
 * 
 * @param   step      The place of the fisrt point in the new line.
*/
void ei_line_rotate_polygon_representation(ei_line_t* polygon, int step);


/**
 * \brief return the coordinates, in the surface, where to anchor the top-left corner of
 *				the rendered text or image.
 *
 *
 */
ei_point_t get_point_from_anchor(ei_anchor_t anchor, ei_rect_t dst_rect, ei_size_t src_size);


/**
 * @brief Returns the ei_rect_t corresponding of the intersection(rectangle inside the two)
 * 		  of both parameters rect1 and rect2.
 */
ei_rect_t ei_intersect_rect(ei_rect_t rect1, ei_rect_t rect2);

/**
 * @brief Returns the ei_rect_t corresponding of the combination
 * 		  of both parameters rect1 and rect2.
 */
ei_rect_t ei_combination_rect(ei_rect_t rect1, ei_rect_t rect2);


/**
 * \brief	Converts the red, green, blue and alpha components of a color into a 32 bits integer
 * 		than can be written directly in the memory returned by \ref hw_surface_get_buffer.
 * 		The surface parameter provides the channel order.
 *
 * @param	surface		The surface where to store this pixel, provides the channels order.
 * @param	color		The color to convert.
 *
 * @return 			The 32 bit integer corresponding to the color. The alpha component
 *				of the color is ignored in the case of surfaces that don't have an
 *				alpha channel.
 */
uint32_t ei_map_rgba(ei_surface_t surface, ei_color_t color);


/**
 * \brief
 *
 * @param	surface
 * @param	color
 *
 * @return
 */
ei_color_t ei_unmap_rgba(ei_surface_t surface, uint32_t color32);
