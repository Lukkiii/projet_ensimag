#include "ei_impl_draw_utils.h"



/**
 * @brief the min between int a and b
*/
int min(int a, int b){return b < a ? b : a;}

/**
 * @brief the max between int a and b
*/
int max(int a, int b){return b > a ? b : a;}

void set_to_channel_not_used(int r, int g, int b, int *a){
    *a = 6 - (r + g + b); // maths
}


void free_line(ei_line_t* line){
    free(line->points);
    free(line);
}


typedef struct ei_point_chained_t{
    ei_point_t point;
    struct ei_point_chained_t* next;
}ei_point_chained_t;


/**
 * \brief Return a line discribing a quarter of a circle (x and y positive).
 *        It is ordered from {0, r} to {r, 0}.
 * 
 * @param   radius      The radius of said circle. 
 * 
 * @param   step        The step between each point on the arc in pixels,
 *                      maximum precision is one, for large circle this
 *                      should be higher for performance.
*/
ei_line_t* create_line_arc(int radius, int step){
    if(step < 1){
        step = 1;
    }
    // default return if radius == 0;
    if(radius == 0){
        ei_line_t* arc = malloc(sizeof(ei_line_t));
        arc->length = 1;
        arc->points = malloc(sizeof(ei_point_t));
        arc->points[0] = (ei_point_t){0, 0};
        return arc;
    }

    // calculate arc
    int n = 0;
    int x = 0;
    int y = radius;
    int x2 = 0;
    int r2 = radius*radius;
    int y2 = r2;

    ei_point_chained_t* point_list = NULL;
    ei_point_chained_t* new_point = NULL;

    while(x < y){
        /* add a point to the list */
        new_point = malloc(sizeof(ei_point_chained_t));
        new_point->point = (ei_point_t){x, y};
        new_point->next = point_list;
        point_list = new_point;
        n++;

        /* find next point on the circle*/
        /* => x++; => x2 = x*x */
        x += step;
        x2 = x*x;
        while((r2 - x2 <= y2) && (0 < y))
        {
            /* => y--; => y2 = y*y */
            y2 -= y;
            y--;
            y2 -= y;
        }
        /* => y++; => y2 = y*y */
        y2 += y;
        y++;
        y2 += y;
    }

    // allocate line space
    ei_line_t* arc = malloc(sizeof(ei_line_t));
    arc->length = 2*n;
    arc->points = malloc(arc->length*sizeof(ei_point_t));

    // construct line from chained list
    for(int i = n-1; i>=0; i--){
        new_point = point_list;
        arc->points[i].x = new_point->point.x;
        arc->points[i].y = new_point->point.y;
        arc->points[2*n-1-i].x = new_point->point.y;
        arc->points[2*n-1-i].y = new_point->point.x;
        point_list = new_point->next;
        free(new_point);
    }

    return arc;
}





/**
 * \brief Retrun a line discribing a smooth rectangle
 * as a polygon (for draw polygon), 
 * return max smoothing rectangle if parameter is too big.
 * 
 * @param   rectangle    The rectangle to smooth.
 * 
 * @param   corner_radius    The radius of 90 degree arc in each corner of the smoothed rectangle.
*/
ei_line_t* create_line_smooth_rect(ei_rect_t rectangle, int corner_radius){
    /* set to max smoothing is the smoothing is too big */
    if(rectangle.size.width  < 2*corner_radius){corner_radius = rectangle.size.width/2;}
    if(rectangle.size.height < 2*corner_radius){corner_radius = rectangle.size.height/2;}
    
    /* create border arc */
    ei_line_t* arc = create_line_arc(corner_radius, corner_radius/10);
    ei_point_t* arc_pts = arc->points;

    /* calculate arcs origin coords */
    int arc_x_left   = rectangle.top_left.x + corner_radius;
    int arc_y_top    = rectangle.top_left.y + corner_radius;
    int arc_x_right  = rectangle.top_left.x + rectangle.size.width - corner_radius;
    int arc_y_bottom = rectangle.top_left.y + rectangle.size.height - corner_radius;

    /* allocate memory */
    ei_line_t* smooth_rect = malloc(sizeof(ei_line_t));
    smooth_rect->length = 1+4*arc->length;
    ei_point_t* sr_pts = smooth_rect->points = malloc(smooth_rect->length*sizeof(ei_point_t));

    /* create smooth rect */
    for(int i = 0; i < arc->length; i++){
        /* top left */
        sr_pts[i].x = arc_x_left - arc_pts[i].x;
        sr_pts[i].y = arc_y_top  - arc_pts[i].y;

        /* bottom left */
        sr_pts[2*arc->length - 1 - i].x = arc_x_left   - arc_pts[i].x;
        sr_pts[2*arc->length - 1 - i].y = arc_y_bottom + arc_pts[i].y;

        /* bottom right */
        sr_pts[2*arc->length + i].x = arc_x_right  + arc_pts[i].x;
        sr_pts[2*arc->length + i].y = arc_y_bottom + arc_pts[i].y;

        /* top right */
        sr_pts[4*arc->length - 1 - i].x = arc_x_right + arc_pts[i].x;
        sr_pts[4*arc->length - 1 - i].y = arc_y_top   - arc_pts[i].y;
    }
    /* its a polygon */
    sr_pts[smooth_rect->length-1] = sr_pts[0];

    /* free arc */
    free_line(arc);

    return smooth_rect;
}

/**
 * \brief Retrun a line discribing a smooth rectangle
 * as a polygon (for draw polygon), 
 * return max smoothing rectangle if parameter is too big.
 * 
 * @param   rectangle    The rectangle to smooth.
 * 
 * @param   corner_radius    The radius of 90 degree arc in each corner of the smoothed rectangle.
*/
ei_line_t* create_line_smooth_rect_top(ei_rect_t rectangle, int corner_radius){
    /* set to max smoothing is the smoothing is too big */
    if(rectangle.size.width  < 2*corner_radius){corner_radius = rectangle.size.width/2;}
    if(rectangle.size.height < 2*corner_radius){corner_radius = rectangle.size.height/2;}
    
    /* create border arc */
    ei_line_t* arc = create_line_arc(corner_radius, corner_radius/10);
    ei_point_t* arc_pts = arc->points;

    /* calculate arcs origin coords */
    int arc_x_left   = rectangle.top_left.x + corner_radius;
    int arc_y_top    = rectangle.top_left.y + corner_radius;
    int arc_x_right  = rectangle.top_left.x + rectangle.size.width - corner_radius;

    /* allocate memory */
    ei_line_t* smooth_rect_top = malloc(sizeof(ei_line_t));
    smooth_rect_top->length = 3+2*arc->length;
    ei_point_t* srt_pts = smooth_rect_top->points = malloc(smooth_rect_top->length*sizeof(ei_point_t));

    /* create smooth rect */
    for(int i = 0; i < arc->length; i++){
        /* top right */
        srt_pts[arc->length -1 - i].x = arc_x_right + arc_pts[i].x;
        srt_pts[arc->length -1 - i].y = arc_y_top   - arc_pts[i].y;
        
        /* top left */
        srt_pts[arc->length + i].x = arc_x_left - arc_pts[i].x;
        srt_pts[arc->length + i].y = arc_y_top  - arc_pts[i].y;
    }
    /* bottom left */
    srt_pts[smooth_rect_top->length-3] = ei_point(rectangle.top_left.x                       , rectangle.top_left.y + rectangle.size.height);
    /* bottom right */
    srt_pts[smooth_rect_top->length-2] = ei_point(rectangle.top_left.x + rectangle.size.width, rectangle.top_left.y + rectangle.size.height);
    /* its a polygon */
    srt_pts[smooth_rect_top->length-1] = srt_pts[0];

    /* free arc */
    free_line(arc);

    return smooth_rect_top;
}


/**
 * \brief Rotate the representation of the polygon by step.
 * 
 * @param   polygon   The polygon to rotate (line with last = first).
 * 
 * @param   step      The place of the fisrt point in the new line.
*/
void ei_line_rotate_polygon_representation(ei_line_t* polygon, int step){
    step = step % (polygon->length-1);
    ei_point_t* new = malloc(polygon->length*sizeof(ei_point_t));
    for(int i = 0; i < polygon->length-1 - step; i++){
        new[i] = polygon->points[i + step];
    }
    for(int i = 0; i < step; i++){
        new[i + polygon->length-1 - step] = polygon->points[i];
    }
    new[polygon->length-1] = new[0];
    free(polygon->points);
    polygon->points = new;
}

ei_point_t get_point_from_anchor(ei_anchor_t anchor, ei_rect_t dst_rect, ei_size_t src_size)
{
    ei_point_t position = dst_rect.top_left;
    // x
    switch (anchor)
    {
    // centered
    case ei_anc_north:
    case ei_anc_center:
    case ei_anc_south:
        position.x += (dst_rect.size.width - src_size.width + 1) / 2;
        break;
    // east
    case ei_anc_northeast:
    case ei_anc_east:
    case ei_anc_southeast:
        position.x += dst_rect.size.width - src_size.width;
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
        position.y += (dst_rect.size.height - src_size.height + 1) / 2;
        break;
    // south
    case ei_anc_southwest:
    case ei_anc_south:
    case ei_anc_southeast:
        position.y -= dst_rect.size.height - src_size.height;
    // north
    default:
        break;
    }
    return position;
}

ei_rect_t ei_intersect_rect(ei_rect_t rect1, ei_rect_t rect2){

    /* calculate top_left point */
    ei_point_t top_left = ei_point(
        max(rect1.top_left.x, rect2.top_left.x),
        max(rect1.top_left.y, rect2.top_left.y));

    /* calculate size */
    ei_size_t size = ei_size(
        min(rect1.top_left.x + rect1.size.width, rect2.top_left.x + rect2.size.width) - top_left.x,
        min(rect1.top_left.y + rect1.size.height, rect2.top_left.y + rect2.size.height) - top_left.y);

    if (size.height < 0 || size.width < 0)
    {
        size.height = 0;
        size.width = 0;
    }

    return ei_rect(top_left, size);
}

ei_rect_t ei_combination_rect(ei_rect_t rect1, ei_rect_t rect2){
    ei_rect_t new_rect = {{0, 0},{0, 0}};
    ei_point_t new_bottom_right = ei_point_add(new_rect.top_left, ei_size_as_point(new_rect.size));

    /* top left */
    /* set new top_left.x */
    if(rect1.top_left.x < rect2.top_left.x){
        new_rect.top_left.x = rect1.top_left.x;
    }else{
        new_rect.top_left.x = rect2.top_left.x;
    }
    /* set new top_left.y */
    if (rect1.top_left.y < rect2.top_left.y){
        new_rect.top_left.y = rect1.top_left.y;
    }else{
        new_rect.top_left.y = rect2.top_left.y;
    }

    /* bottom right */
    ei_point_t bottom_right_rect1 = ei_point_add(rect1.top_left, ei_size_as_point(rect1.size));
    ei_point_t bottom_right_rect2 = ei_point_add(rect2.top_left, ei_size_as_point(rect2.size));
    /* set new bottom_right.x */
    if (bottom_right_rect1.x > bottom_right_rect2.x){
        new_bottom_right.x = bottom_right_rect1.x;
    }else{
        new_bottom_right.x = bottom_right_rect2.x;
    }
    /* set new bottom_right.y */
    if (bottom_right_rect1.y > bottom_right_rect2.y){
        new_bottom_right.y = bottom_right_rect1.y;
    }else{
        new_bottom_right.y = bottom_right_rect2.y;
    }

    new_rect.size.width = new_bottom_right.x - new_rect.top_left.x;
    new_rect.size.height = new_bottom_right.y - new_rect.top_left.y;
    return new_rect;
}

uint32_t ei_map_rgba(ei_surface_t surface, ei_color_t color)
{

    int id_red, id_green, id_blue, id_alpha;
    uint32_t color32 = 0;

    hw_surface_get_channel_indices(surface, &id_red, &id_green, &id_blue, &id_alpha);
    color32 += color.red << (id_red * 8);
    color32 += color.green << (id_green * 8);
    color32 += color.blue << (id_blue * 8);

    if (id_alpha != -1)
    {
        color32 += color.alpha << (id_alpha * 8);
    }
    return color32;
}

ei_color_t ei_unmap_rgba(ei_surface_t surface, uint32_t color32)
{
    ei_color_t color;
    int id_red, id_green, id_blue, id_alpha;

    if (surface != NULL)
    {
        hw_surface_get_channel_indices(surface, &id_red, &id_green, &id_blue, &id_alpha);
    }
    else
    {
        id_red = 3;
        id_green = 2;
        id_blue = 1;
        id_alpha = 0;
    }

    color.red = (unsigned char)(color32 >> (id_red * 8));
    color.green = (unsigned char)(color32 >> (id_green * 8));
    color.blue = (unsigned char)(color32 >> (id_blue * 8));

    if (id_alpha != -1)
    {
        color.alpha = (unsigned char)color32 >> (id_alpha * 8);
    }
    return color;
}
