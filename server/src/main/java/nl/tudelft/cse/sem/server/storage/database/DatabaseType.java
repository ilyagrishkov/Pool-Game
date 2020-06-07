package nl.tudelft.cse.sem.server.storage.database;

import nl.tudelft.cse.sem.server.storage.CreationCommand;

public enum DatabaseType {
    SQL(SQLDatabase::new);

    private CreationCommand<Database> command;

    DatabaseType(CreationCommand<Database> command) {
        this.command = command;
    }

    /**
     * Returns an instance of a {@link Database}.
     *
     * @return Instance of {@link Database}
     */
    public Database create() {
        return this.command.execute();
    }
}
