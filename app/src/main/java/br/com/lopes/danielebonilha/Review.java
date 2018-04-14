package br.com.lopes.danielebonilha;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Dani on 24/07/2016.
 * A função dessa classe é fornecer os campos para a inicialização e
 * manipulação do conteudo do objeto Json.
 */
public class Review {

    public final String convertedDate;
    public final String movieTitle;
    public final String reviewAuthor;
    public final String iconURL;
    public final String reviewDescription;
    public final String linkReview;
    public final String linkTitle;

    public Review(String date, String movieTitle, String reviewAuthor, String iconURL, String reviewDescription, String linkReview, String linkTitle){

        this.convertedDate = formatDateLocale(date);
        this.movieTitle = movieTitle;
        this.reviewAuthor = reviewAuthor.toUpperCase();
        this.iconURL = iconURL;
        this.reviewDescription = reviewDescription;
        this.linkReview = linkReview;
        this.linkTitle = linkTitle;
    }

    /**
     * Este metodo retorna uma String com o conteúdo
     * de reviewDescription.
     * @return short review summary
     */
    public String getReviewDescription() {
        return reviewDescription;
    }

    /**
     * Este metodo retorna uma String com o conteúdo
     * de linkReview.
     * @return url review
     */
    public String getLinkReview() {
        return linkReview;
    }

    /**
     * Este metodo retorna uma String com o conteúdo
     * de linkTitle.
     * @return suggested link title
     */
    public String getLinkTitle() {
        return linkTitle;
    }

    /**
     * Este metodo recebe o campo publication_date do Objeto JSon e
     * formata a data de acordo com o idioma do dispositivo.
     * @param reviewDate string obtida do objeto json no campo publication_date
     * @return string contendo a data formatada
     */
    private static String formatDateLocale(String reviewDate){

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormatter.parse(reviewDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }
}
