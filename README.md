# Flux
Last updated on July 14, 2016


### Fluctuating Repetitions
This project is based on one that comes from [this blog](http://koaning.io/fluctuating-repetition.html) post by Vincent D. Warmerdam.
In it, Warmerdam provides a really interesting set of equations which iterateively describe the progression of a point through space.
If we iterate through enough values of Z, we are drawing an intensity image of the relative distribution of the range of the function.
*Note that there is a slight error in Warmerdam's code, he has miswritten two cosine functions as sine functions.  Fixing this (as I do)
breaks the symmetry that he describes, but beyond that, does not meaningfully impact the project.*

## Parameters
The model takes in 6 parameters, a, b, c, d, e, and f.
After playing around for a bit, I wondered if we could find a way to animate the shapes that appear so vibrant in the still images that
Warmerdam creates.
I realized that given a set of six parameters and the image it creates, any small changes in the parameters maintained enough similarity 
to the original image to be percieved as motion.
However, moving parameters about as a random walk led to motion that jerked about and did not produce pleasant animations.

### Fixing Irradic Motion via Physics
To allow smooth transitions within the parameter space in a way that was still quazi random, I created a model that used physics to describe
the parameter space.
I imagined the set of parameters as a six dimensional position, with a six dimensional velocity and acceleration (where acceleration was determined
at random).
The result was a continuous and differentiable trajactory within the parameter space which translated more fluid and elegant animated results.

### Good Parameter Space, Bad Parameter Space
After repeated work with this motion, I realized that when all of the parameters are at low values (between 0 and .7, generally), 
the result is generally uninteresting; a warped quadrilateral, appearing like a flag in the wind.
Coorespondingly, when the sum of the parameters was too high (in the neighborhood of 8-15), the result quickly became completely diffuse,
Relatively unremarkable.
I sought out a technique that would avoid both of these spaces.

### Orbits - Physics again
A simple solution to this cannundrum was modifying the physical situation to describe one of a unit orbit: a circular (in 6 dimensions)
path which maintained a distance of 1 from the origin of 0. I then shifted this result to fit into the range [.5, 1.7], which avoided both
of the bad parameter spaces described, and of course created even more continuous animated results.

## Looping and Colors
One nice feature of an orbit is that it ends where it starts. 
For GIF files, this is a nice property, as it allows them to loop continuously in a way that is pleasing to watch.
Moreover, starting multiple parameter sets in the same location, and giving their velocity different directions yields paths which 
mutually intersect at both the origin and at a point opposite the origin on the sphere of orbit. 
Using different colors to map these different parameter orbits (and some math to combine them back into a single GIF) yields some stunning results.

Please feel free to email me with any ideas or questions!
