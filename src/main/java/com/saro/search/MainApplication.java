package com.saro.search;

import java.util.Scanner;

import static com.saro.search.WebSearch.buildSearchQuery;
import static com.saro.search.WebSearch.printTopJSLibraries;

public class MainApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Search Query \n");
        String query = scanner.nextLine();

        System.out.print("Enter Search Results \n");
        int numOfResults = scanner.nextInt();

        printTopJSLibraries(buildSearchQuery(query, numOfResults));
    }
}
