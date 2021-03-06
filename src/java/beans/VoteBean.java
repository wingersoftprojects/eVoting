/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Candidate;
import entities.EVotingPersistentManager;
import entities.Vote;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.orm.PersistentException;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import utilities.GeneralUtilities;

/**
 *
 * @author bajuna
 */
@ManagedBean
@SessionScoped
public class VoteBean extends AbstractBean<Vote> implements Serializable {

    private List<Vote> votes;
    private BarChartModel votesModel;
    private PieChartModel pieModel;

    public VoteBean() {
        super(Vote.class);
    }

    @Override
    public void init() {
        if (super.getEntityClass() == null) {
            loginBean.logout();
        }
    }
    @ManagedProperty("#{loginBean}")
    private LoginBean loginBean;

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    /**
     * @return the votes
     */
    public List<Vote> getVotes() {
        return votes;
    }

    /**
     * @param votes the votes to set
     */
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    /**
     * @return the votesModel
     */
    public BarChartModel getVotesModel() {
        GeneralUtilities.clearsession();
        BarChartModel model = new BarChartModel();
        ChartSeries ser1 = new ChartSeries();
        ser1.setLabel("VOTES");

        try {
            String qString = "SELECT v.candidate,count(v.candidate) FROM Vote v "
                    + "GROUP BY v.candidate";
            List<Object[]> result = EVotingPersistentManager.instance().getSession().createQuery(qString).list();
            for (Object[] resultElement : result) {
                Candidate aCandidate = (Candidate) resultElement[0];
                long number = (long) resultElement[1];
                ser1.set(aCandidate.getVoter().getSur_name() + " " + aCandidate.getVoter().getGiven_names(), number);

            }
        } catch (PersistentException | NullPointerException ex) {
            Logger.getLogger(VoteBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage("List", new FacesMessage(ex.getMessage()));
            GeneralUtilities.clearsession();
        }

        model.addSeries(ser1);

        votesModel = model;
        votesModel.setLegendPosition("e");
        votesModel.setShowPointLabels(true);
        votesModel.getAxes().put(AxisType.X, new CategoryAxis("CANDIDATE"));
        Axis yAxis = votesModel.getAxis(AxisType.Y);
        yAxis = votesModel.getAxis(AxisType.Y);
        yAxis.setLabel("VOTES");
        yAxis.setMin(0);
        yAxis.setMax(50);

        return votesModel;
    }

    /**
     * @param votesModel the votesModel to set
     */
    public void setVotesModel(BarChartModel votesModel) {
        this.votesModel = votesModel;
    }

    public int refreshVotes() {
        GeneralUtilities.clearsession();
        votes = super.getTs2();
        return votes.size();
    }

    /**
     * @return the pieModel
     */
    public PieChartModel getPieModel() {
        GeneralUtilities.clearsession();
        pieModel = new PieChartModel();
        try {
            String qString = "SELECT v.candidate,count(v.candidate) FROM Vote v "
                    + "GROUP BY v.candidate ORDER BY count(v.candidate) DESC";
            List<Object[]> result = EVotingPersistentManager.instance().getSession().createQuery(qString).list();
            for (Object[] resultElement : result) {
                Candidate aCandidate = (Candidate) resultElement[0];
                long number = (long) resultElement[1];
                pieModel.set(aCandidate.getVoter().getSur_name() + " " + aCandidate.getVoter().getGiven_names(), number);
            }
        } catch (PersistentException | NullPointerException ex) {
            Logger.getLogger(VoteBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage("List", new FacesMessage(ex.getMessage()));
            GeneralUtilities.clearsession();
        }
        pieModel.setTitle("");
        pieModel.setLegendPosition("e");
        pieModel.setFill(true);
        pieModel.setShowDataLabels(true);
        pieModel.setDiameter(250);
        return pieModel;
    }

    /**
     * @param pieModel the pieModel to set
     */
    public void setPieModel(PieChartModel pieModel) {
        this.pieModel = pieModel;
    }
}
