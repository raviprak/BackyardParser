package backyard.scraper;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;

import org.htmlparser.util.ParserException;

import backyard.pageparser.BackyardPage;

/**
 * Scrapes the backyard tree and stores it into a file so that the tree may be
 * easily analysed using Hadoop.
 * 
 * @author raviprak
 */
class ScrapeBackyardTask implements Runnable {
	static String cookie = "";

	/**
	 * Homebrew. Could use apache's HttpClient but wth
	 * 
	 * @param url
	 * @return
	 */
	private String getURLString(String url) {
		String toRet = "";
		try {
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("socks.yahoo.com", 1080));
			URLConnection urlConnection = new URL(url).openConnection(proxy);
			String YBYCookie = "YBY=" + cookie;
			urlConnection.setRequestProperty("Cookie", YBYCookie);
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				toRet += line;
			}
			br.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return toRet;
	}

	private String username;
	public static int byteCount = 0;
	public static int pageCount = 0;
	public static Writer writer;
	public static HashSet<String> usersAlreadyDone;
	public static ThreadPoolExecutor myThreadPool;
	private static Object writerLock = new Object();
	private static Object hashSetLock = new Object();

	public ScrapeBackyardTask(String usern) {
		this.username = usern;
	}

	public void addUserPage() throws IOException, ParserException {
		String page = getURLString("http://backyard.yahoo.com/tools/g/employee/profile?user_id=" + username);
		synchronized (writerLock) {
			writer.write(username);
			writer.write("\u0001");
			writer.write(page);
			writer.write("\n");
		}
		synchronized (hashSetLock) {
			usersAlreadyDone.add(username);
			byteCount += page.length();
			pageCount++;
			System.out.print("Scraped " + pageCount + " pages containing ");
			System.out.format("%5.2f", ((double) byteCount) / (1024 * 1024));
			System.out.print(" Mb. Added " + username + ". ");
			System.out.println(myThreadPool.getActiveCount() + " thread(s) in pool. ");
		}
		BackyardPage bp = new BackyardPage(page);
		for (String user : bp.directReports) {
			if (!usersAlreadyDone.contains(user)) {
				ScrapeBackyardTask newUserToScrape = new ScrapeBackyardTask(user);
				myThreadPool.submit(newUserToScrape);
			}
		}
	}

	public void run() {
		try {
			this.addUserPage();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		} finally {
			if (myThreadPool.getActiveCount() == 1)
				myThreadPool.shutdown();
		}
	}
}

public class ScrapeBackyard {
	public static void main(String args[]) throws IOException, InterruptedException {
		String topUser = "mmayer";
		if (args.length != 1 && args.length != 2) {
			System.out.println("Usage\n java ScrapeBackyard <YBY COOKIE> [topUser]");
			System.exit(1);
		} else if (args.length == 1) {
			ScrapeBackyardTask.cookie = args[0];
		} else if (args.length == 2) {
			ScrapeBackyardTask.cookie = args[0];
			topUser = args[1];
		}

		HashSet<String> usersAlreadyDone = new HashSet<String>();
		FileWriter scrapedFile = new FileWriter(
				"myScrapedFile-" + topUser + "." + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".dat");
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(40, 40, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

		ScrapeBackyardTask.myThreadPool = threadPoolExecutor;
		ScrapeBackyardTask.usersAlreadyDone = usersAlreadyDone;
		ScrapeBackyardTask.writer = scrapedFile;

		ScrapeBackyardTask scrapeBackyardTask = new ScrapeBackyardTask(topUser);
		threadPoolExecutor.submit(scrapeBackyardTask);
		threadPoolExecutor.awaitTermination(30, TimeUnit.MINUTES);

		scrapedFile.close();
	}
}
