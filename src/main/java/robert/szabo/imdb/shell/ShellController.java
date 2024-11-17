package robert.szabo.imdb.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import robert.szabo.imdb.entity.Actor;
import robert.szabo.imdb.entity.Director;
import robert.szabo.imdb.entity.Movie;
import robert.szabo.imdb.services.MovieQueryBuilder;
import robert.szabo.imdb.services.PersonQueryBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ShellController {
    private MovieQueryBuilder movieQueryBuilder;
    private PersonQueryBuilder personQueryBuilder;
    private StringBuffer outputStr;


    private boolean PrintVerbose;
    private boolean hasError;
    private Scanner scnr;
    private static Stack<String> addPersonQs = new Stack<>();
    private static Stack<String> addMovieQs = new Stack<>();

    @Autowired
    public ShellController(MovieQueryBuilder mqb, PersonQueryBuilder pqb) {
        this.movieQueryBuilder = mqb;
        this.personQueryBuilder = pqb;
        this.outputStr = new StringBuffer();
        this.PrintVerbose = false;
        this.hasError = false;
        scnr = new Scanner(System.in);
        addPersonQs.push("Person Role (Director or Actor): ");
        addPersonQs.push("Person Nationality: ");
        addPersonQs.push("Person Name: ");
        addMovieQs.push("Starring: ");
        addMovieQs.push("Director: ");
        addMovieQs.push("Length: ");
        addMovieQs.push("Title: ");
    }

    private boolean checkArgValidity(String arg) {
        boolean surroundedByQuotes = arg.startsWith("\"") && arg.endsWith("\"");
        boolean isNotAFlag = !arg.startsWith("-");
        return surroundedByQuotes && isNotAFlag;
    }

    private String convertDurationToStr(Long runtime) {
        long hrs = runtime / 3600;
        long mins = (runtime % 3600) / 60;
        long secs = runtime % 60;

        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }
    private Long convertStrToDuration(String s) {
        String split[] = s.split(":");

        Long sum = (long) 0;
        for (int i = 0; i < split.length; i++) {
            try{
                long num = Long.valueOf(split[i]);
                if (i == 0) { //convert hrs
                    sum += num * 3600;
                }
                else if (i == 1) { //convert min
                    sum += num * 60;
                }
                else if (i == 2) { //add sec
                    sum += num;
                }

            } catch (NumberFormatException ex) {
                System.out.println("ERROR: Could not convert the runtime string you gave to a number. Try again!");
            }
        }
        return sum;
    }

    private void evalListingFlags(String flags[]) {
        for (int i = 1; i < flags.length && hasError == false; i++) {
            try {
                switch (flags[i]) {
                    case "-v":
//                        System.out.println("VERBOSE");
                        this.PrintVerbose = true;
                        break;
                    case "-t": // search by title
                        System.out.println("S REGEX");
                        //take the flag argument and check if its valid
                        String titleSearchArg = flags[i+1];
                        if (checkArgValidity(titleSearchArg)) this.movieQueryBuilder.withTitle(titleSearchArg.substring(1, titleSearchArg.length()-1));

                        else {
                            System.out.println("Error: Your flag argument for \"-t\" was found to be invalid");
                            hasError = true;
                        }
                        System.out.println(titleSearchArg);
                        break;
                    case "-d": //search by director
//                        System.out.print("SEARCH BY DIRECTOR REGEX: ");
                        String dirSearchArg = flags[i+1];
                        if (checkArgValidity(dirSearchArg)) this.movieQueryBuilder.withDirector(dirSearchArg.substring(1, dirSearchArg.length()-1));
                        else {
                            System.out.println("Error: Your flag argument for \"-d\" was found to be invalid");
                            hasError = true;
                        }
                        System.out.println(dirSearchArg);
                        break;
                    case "-a": //search by actor
//                        System.out.println("SEARCH BY ACTOR");
                        String actorSearchArg = flags[i+1];
                        if (checkArgValidity(actorSearchArg)) this.movieQueryBuilder.withActor(actorSearchArg.substring(1, actorSearchArg.length()-1));
                        else {
                            System.out.println("Error: Your flag argument for \"-a\" was found to be invalid");
                            hasError = true;
                        }
                        break;
                    case "-la": // list in ascending order
                        this.movieQueryBuilder.withAsc();
                        break;
                    case "-ld":
                        this.movieQueryBuilder.withDesc();
                        break;
                    default:
                        break;
                }

            } catch (ArrayIndexOutOfBoundsException exc) {
                System.out.println("Error! No Argument given with your flag.");
                hasError = true;
            }
        }
    }

    private void evalWritingFlags(String cmd) {
        String pName = "";
        String pNat = "";
        String pRole = "";
        String timeRegex = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";
        String mTitle = "";
        String mDuration = "";
        String mDirector = "";
        Director directorRef = null;
        List<Actor> mActors = new ArrayList<>();
        Stack<String> qs = new Stack<>(); //questions
        if (cmd.equals("-p")) {
            qs.addAll(addPersonQs);
            System.out.println(qs.pop());
            pName = scnr.nextLine();
            System.out.println(qs.pop());
            pNat = scnr.nextLine();
            System.out.println(qs.pop());
            pRole = scnr.nextLine();
            if (!pName.isEmpty() && !pNat.isEmpty() && !pRole.isEmpty()) {
                personQueryBuilder.createNewPerson(pName, pNat, pRole);
            }
            else {
                System.out.println("Error! Add some text for each field.");
            }
        }
        else if (cmd.equals("-m")) { // movie creation input loops
            qs.addAll(addMovieQs);
            String nextQ = qs.pop();
            System.out.println(nextQ);
            mTitle = scnr.nextLine();
            if (mTitle.equalsIgnoreCase("exit")) return;
            //ask for movie runtime (duration)
            nextQ = qs.pop();
            while (!mDuration.matches(timeRegex)) {
                System.out.println(nextQ);
                mDuration = scnr.nextLine();
                if (!mDuration.matches((timeRegex))) {
                    System.out.println("Error: Time should be in this format: HH:MM:SS");
                }
            }
            //ask for director, and check if this name exists in the db
            nextQ = qs.pop();
            while (directorRef == null) {
                System.out.println(nextQ);
                mDirector = scnr.nextLine();
                if (mDirector.equalsIgnoreCase("exit")) return;

                Optional<Director> d = personQueryBuilder.getDirector(mDirector);
                if (d.isPresent()) {
                    directorRef = d.get();
                    System.out.println("Director found!" + directorRef.getName());
                }
                else {
                    System.out.println("Can't find that Director. Try again.");
                }
            }
            //add actors
            nextQ = qs.pop();
            String s = "";
            System.out.println(nextQ);
            do {
                s = scnr.nextLine();
                if (s.equalsIgnoreCase("exit")) break;
                Optional<Actor> a = personQueryBuilder.getActor(s);
                if (a.isPresent()) {
//                    System.out.println("YAY found actor");
                    mActors.add(a.get());
                }
                else {
                    System.out.println("No actor found. Try again.");
                }
            } while (!s.equalsIgnoreCase("exit"));
            // Send query to create the movie
            boolean completed = movieQueryBuilder.handleCreateMovie(new Movie(
                    mTitle,
                    directorRef.getDirId(),
                    directorRef,
                    convertStrToDuration(mDuration),
                    mActors
            ));
            if (completed) {
                outputStr.append("Movie successfully created");
            }
            else {
                outputStr.append("Couldn't create movie!");
            }
        }
    }

    private void doDelete(String cmds[]) {
        System.out.println("DOING DELETE :" + cmds[2]);
        boolean success = personQueryBuilder.deletePerson(cmds[2]);
        if (success) {
            System.out.println("Successfully Deleted: " + cmds[2]);
        }
    }

    /**
     * Evaluates the command and prints the output to the console
     * @param cmd
     */
    private void evalInputCommand(String cmd[]) {
        String mainCmd = cmd[0];

        switch (mainCmd) {
            case "l":
//                System.out.println("PRINT MOVIES OUT");
                if (cmd.length > 1) evalListingFlags(cmd);
                List<Movie> queryResult = movieQueryBuilder.buildMovieSelection();
                if (queryResult.size() < 1) {
                    outputStr.append("No films of this criteria found. Try again");
                }
                if (!hasError) {
                    for (Movie m : queryResult) {
                        outputStr.append(m.getTitle() + " " + convertDurationToStr(m.getDuration()) + "\n");
                        if (this.PrintVerbose == true) {
                            outputStr.append("\tSTARRING:\n");
                            List<Actor> actors = m.getActors();
                            actors.forEach(a -> outputStr.append("\t\t" + a.getName() + "\n"));
                        }
                    }
                }
                break;
            case "a":
//                System.out.println("ADD SOMETHING TO DB");
                if (cmd.length == 2) evalWritingFlags(cmd[1]);
                else outputStr.append("Please Specify one '-m' or '-p' flag to add a movie or person");
                break;
            case "d":
                if (cmd.length == 3) doDelete(cmd);
                else {
                    System.out.println(cmd.length);

                    outputStr.append("ERROR: Use \'d -p \"(person name)\"\' to delete a person (actor or director)");
                }
                break;
            default:
                outputStr.append("NOT YET IMPLEMENTED");
                break;
        }
    }

    private static String[] splitCommand(String input) {
        // Match sequences outside quotes or within quotes
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|\\S+");
        Matcher matcher = pattern.matcher(input);

        ArrayList<String> result = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Add the quoted string without quotes
                result.add(matcher.group(1));
            } else {
                // Add the unquoted word
                result.add(matcher.group());
            }
        }

        return result.toArray(new String[0]);
    }


    public void execute() {
        System.out.printf("WELCOME TO THE IMDB! Here are the commands:\n");
        System.out.println("LISTING MOVIES: ");
        System.out.println("\t \"l\": list movies (in ascending runtime)");
        System.out.println("\t FLAGS: -v: Verbose Listing\t-t \"title\": search by Title (must be in quotes)\t-d \"director\": Search by director\t-a \"actor\": search by actor");
        System.out.println("ADDING CONTENT: ");
        System.out.println("\t 'm' command with FLAGS: '-p' (add person), '-m' (add movie)");
        System.out.println("DELETING CONTENT:\n\t d -p \"Person Name\"\nDelete a person (actor or director) by using 'd -p \"person name\"");
        for (;;) {
            System.out.print("> ");
            String cmd = scnr.nextLine();
            if (cmd.length() > 0) {
                evalInputCommand(splitCommand(cmd));
            }
            // PRINT STRING BUFFER
            System.out.println(outputStr);
            // CLEAR STRING BUFFER
            outputStr.setLength(0);
            // reset values for the next command
            this.PrintVerbose = false;
            this.hasError = false;

        }
    }
}
