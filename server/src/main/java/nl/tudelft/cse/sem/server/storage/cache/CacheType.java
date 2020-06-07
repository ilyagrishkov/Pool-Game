package nl.tudelft.cse.sem.server.storage.cache;

import nl.tudelft.cse.sem.server.storage.CreationCommand;

public enum CacheType {
    MEMORY(InMemoryCache::new);

    private CreationCommand<Cache> command;

    CacheType(CreationCommand<Cache> command) {
        this.command = command;
    }

    /**
     * Returns an instance of a {@link Cache}.
     *
     * @return Instance of {@link Cache}
     */
    public Cache create() {
        return this.command.execute();
    }
}
