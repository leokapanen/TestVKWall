package leokapanen.wall;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import leokapanen.core.Conf;
import leokapanen.datamodel.WallData;
import leokapanen.login.Auth;
import leokapanen.login.LoginFragment;
import leokapanen.testvkwall.IWallActivity;
import leokapanen.testvkwall.R;

/**
 * Created by Leonid Kabanen on 15.07.15.
 */
public class WallFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<WallData>> {

    @Bind(R.id.wall_list)
    ListView wallListView;

    @Bind(R.id.swipy_refresh_layout)
    SwipyRefreshLayout swipyRefreshLayout;

    List<WallData> wallDataList = new ArrayList<>();

    private boolean isWallLoading = false;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wall_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        WallListAdapter adapter = new WallListAdapter(getActivity(), wallDataList);
        wallListView.setAdapter(adapter);

        wallListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (((firstVisibleItem + visibleItemCount) == totalItemCount) && (firstVisibleItem != 0)) {
                    loadNextWallBlock();
                }
            }
        });

        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                switch (direction) {
                    case TOP:
                        reloadWall();
                        break;

                    case BOTTOM:
                        loadNextWallBlock();
                        break;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showLoadingDialog();

        Bundle bundle = new Bundle();
        bundle.putInt(Conf.KEY_BUNDLE_OFFSET, 0);
        bundle.putBoolean(Conf.KEY_BUNDLE_FORCE_UPDATE, true);
        getLoaderManager().initLoader(Conf.LOADER_WALL, bundle, this);
        getLoaderManager().getLoader(Conf.LOADER_WALL).forceLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((IWallActivity) getActivity()).setTitle(getString(R.string.title_wall));
    }

    public synchronized boolean isWallLoading() {
        return isWallLoading;
    }

    public synchronized void setWallLoading(boolean isWallLoading) {
        this.isWallLoading = isWallLoading;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.wall_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                Auth.INSTANCE.logout();
                ((IWallActivity) getActivity()).switchFragment(new LoginFragment());
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private synchronized void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
            progressDialog.setMessage(getString(R.string.msg_loading));
            progressDialog.show();
        }
    }

    private synchronized void dismissLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private synchronized void reloadWall() {
        if (!isWallLoading()) {
            setWallLoading(true);
            Bundle bundle = new Bundle();
            bundle.putInt(Conf.KEY_BUNDLE_OFFSET, 0);
            bundle.putBoolean(Conf.KEY_BUNDLE_FORCE_UPDATE, true);

            getLoaderManager().restartLoader(Conf.LOADER_WALL, bundle, this);
            getLoaderManager().getLoader(Conf.LOADER_WALL).forceLoad();
        }
    }

    private synchronized void loadNextWallBlock() {
        if (!isWallLoading() && (wallDataList.size() < Conf.WALL_MAX_LIMIT)) {
            setWallLoading(true);
            Bundle bundle = new Bundle();
            bundle.putInt(Conf.KEY_BUNDLE_OFFSET, wallDataList.size());
            bundle.putBoolean(Conf.KEY_BUNDLE_FORCE_UPDATE, false);

            getLoaderManager().restartLoader(Conf.LOADER_WALL, bundle, this);
            getLoaderManager().getLoader(Conf.LOADER_WALL).forceLoad();
        }
    }

    @Override
    public Loader<List<WallData>> onCreateLoader(int id, Bundle args) {
        Loader<List<WallData>> loader = null;
        switch (id) {
            case Conf.LOADER_WALL:
                loader = new WallLoader(getActivity(), args);
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<WallData>> loader, List<WallData> records) {
        switch (loader.getId()) {
            case Conf.LOADER_WALL:
                dismissLoadingDialog();

                if (((WallLoader) loader).isForseUpdate()) {
                    wallDataList.clear();
                }

                wallDataList.addAll(records);
                ((ArrayAdapter<WallData>) wallListView.getAdapter()).notifyDataSetChanged();

                swipyRefreshLayout.setRefreshing(false);
                setWallLoading(false);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<WallData>> loader) {
        swipyRefreshLayout.setRefreshing(false);
        dismissLoadingDialog();
        setWallLoading(false);
    }

}
