package derenvural.sourceread_prototype.ui.article;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import derenvural.sourceread_prototype.data.cards.articles.Article;

public class ArticleViewModel extends ViewModel {
    private MutableLiveData<Article> mArticle;

    public ArticleViewModel(){
        mArticle = new MutableLiveData<Article>();
        setArticle(null);
    }

    // SET
    public void setArticle(Article s) {
        mArticle.setValue(s);
    }

    // GET
    public LiveData<Article> getArticle() {
        return mArticle;
    }
}
