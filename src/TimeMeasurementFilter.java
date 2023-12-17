import java.io.IOException;
import jakarta.servlet.annotation.WebFilter;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletRequest;

@WebFilter(urlPatterns = {"/api/single-genre"})
public class TimeMeasurementFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(TimeMeasurementFilter.class.getName());

    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long servletStartTime = System.nanoTime();

        // Time before JDBC execution
        long jdbcStartTime = System.nanoTime();

        // Proceed with the chain (invoke the servlet)
        chain.doFilter(request, response);

        // Time after JDBC execution
        long jdbcEndTime = System.nanoTime();

        long servletEndTime = System.nanoTime();

        long servletElapsedTime = servletEndTime - servletStartTime;
        long jdbcElapsedTime = jdbcEndTime - jdbcStartTime;

        // Log the elapsed time for TS and TJ
        System.out.println("search servlet total execution time (TS): " + servletElapsedTime + " nanoseconds");
        System.out.println("JDBC execution time (TJ): " + jdbcElapsedTime + " nanoseconds");

        // Get the current working directory
        try {
            // Create a File object for the new text file
            File newTextFile = new File("/home/ubuntu/log_file.txt");
            System.out.println(newTextFile);

            // Create the file if it doesn't exist
            if (newTextFile.createNewFile()) {
                System.out.println("Text file created: " + newTextFile.getName());
            } else {
                System.out.println("Text file already exists.");
            }

            // Write measurements to a file if needed
            FileWriter fileWriter = new FileWriter(newTextFile, true);
            try {
                fileWriter.write("search servlet total execution time (TS): " + servletElapsedTime + " nanoseconds\n");
                fileWriter.write("JDBC execution time (TJ): " + jdbcElapsedTime + " nanoseconds\n");
            } catch (IOException e) {
                // Handle IOException
                e.printStackTrace();
            } finally {
                // Close the FileWriter in the finally block
                if (fileWriter != null) {
                    try {
                        fileWriter.close(); // Close the FileWriter
                    } catch (IOException e) {
                        e.printStackTrace(); // Handle exceptions while closing
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the text file.");
            e.printStackTrace();
        }
    }

    public void destroy() {
        // Clean-up code if needed
    }
}