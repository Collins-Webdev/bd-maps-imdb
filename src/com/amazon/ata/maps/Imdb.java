package com.amazon.ata.maps;

import java.util.*;

/**
 * Stores the relationships between movies and actors, allowing releasing a new movie
 * with all actors in the cast, adding a single actor to an existing (or new) movie,
 * unreleasing a movie completely, and querying actors by movie and vice versa.
 */
public class Imdb {
    private Map<Movie, Set<Actor>> movieToActors;
    private Map<Actor, Set<Movie>> actorToMovies;

    public Imdb() {
        movieToActors = new HashMap<>();
        actorToMovies = new HashMap<>();
    }

    /**
     * Adds the new movie to the set of movies that an actor has appeared in.
     * If the movie already exists in the database, this will overwrite actors
     * associated with the movie with the new values provided.
     *
     * @param movie the movie being released
     * @param actors a set of actors that appear in the movie
     */
    public void releaseMovie(Movie movie, Set<Actor> actors) {
        movieToActors.put(movie, new HashSet<>(actors));
        for (Actor actor : actors) {
            actorToMovies.computeIfAbsent(actor, k -> new HashSet<>()).add(movie);
        }
    }

    /**
     * Removes the given movie from the database, including any actors
     * credited in the movie.
     *
     * @param movie the movie to remove
     * @return true if the movie was removed, false if it wasn't in Imdb
     *         to begin with
     */
    public boolean removeMovie(Movie movie) {
        Set<Actor> actors = movieToActors.remove(movie);
        if (actors == null) {
            return false;
        }
        for (Actor actor : actors) {
            actorToMovies.get(actor).remove(movie);
            if (actorToMovies.get(actor).isEmpty()) {
                actorToMovies.remove(actor);
            }
        }
        return true;
    }

    /**
     * Adds a new movie to the set of movies that an actor has appeared in.
     * If the movie already exists in the database, will add the actor
     * if they haven't been added already. If the movie doesn't yet exist
     * in the database, this will add the movie with the actor as the only
     * credit.
     *
     * @param movie the movie to add to the actors set of movies
     * @param actor the actor that appears in this movie
     */
    public void tagActorInMovie(Movie movie, Actor actor) {
        movieToActors.computeIfAbsent(movie, k -> new HashSet<>()).add(actor);
        actorToMovies.computeIfAbsent(actor, k -> new HashSet<>()).add(movie);
    }

    /**
     * Returns a set of actors who are credited in the given movie. If a movie is not
     * released on IMDB throw an IllegalArgumentException.
     *
     * @param movie the movie to get actors for
     * @return the set of actors who are credited in the passed in movie
     */
    public Set<Actor> getActorsInMovie(Movie movie) {
        if (!movieToActors.containsKey(movie)) {
            throw new IllegalArgumentException("Movie not found in IMDB");
        }
        return new HashSet<>(movieToActors.get(movie));
    }

    /**
     * Returns a set of movies that the specified actor has appeared in. If the
     * actor has not appeared in any movies, return an empty Set.
     *
     * @param actor the actor to get movies for
     * @return the set of movies that the passed in actor has appeared in
     */
    public Set<Movie> getMoviesForActor(Actor actor) {
        return new HashSet<>(actorToMovies.getOrDefault(actor, Collections.emptySet()));
    }

    /**
     * Returns all actors that IMDB has in its records as having appeared in a movie.
     *
     * @return a set of actors that IMDB has as appeared in movies
     */
    public Set<Actor> getAllActorsInIMDB() {
        return new HashSet<>(actorToMovies.keySet());
    }

    /**
     * Returns the total number of individual movie-actor pairs in the database.
     *
     * So if there are 2 movies, the first movie has 1 actor and the second one
     * has 6 actors, this method will return 7.
     *
     * @return The total number of movie-actor pairings: the number of times
     *         any actor has appeared in any movie
     */
    public int getTotalNumCredits() {
        return actorToMovies.values().stream().mapToInt(Set::size).sum();
    }
}