package derenvural.sourceread_prototype.ui.article;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import derenvural.sourceread_prototype.data.cards.articles.Article;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class ArticleViewModel extends ViewModel {
    private MutableLiveData<Article> mArticle;
    private MutableLiveData<LoggedInUser> mUser;

    public ArticleViewModel(){
        mArticle = new MutableLiveData<Article>();
        setArticle(null);
        mUser = new MutableLiveData<LoggedInUser>();
        setUser(null);
    }

    // SET
    public void setArticle(Article s) {
        mArticle.setValue(s);
    }
    public void setUser(LoggedInUser s) {
        mUser.setValue(s);
    }

    // GET
    public LiveData<Article> getArticle() {
        return mArticle;
    }
    public LiveData<LoggedInUser> getUser() {
        return mUser;
    }
}
