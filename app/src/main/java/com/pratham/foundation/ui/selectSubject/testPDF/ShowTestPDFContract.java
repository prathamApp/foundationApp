package com.pratham.foundation.ui.selectSubject.testPDF;

import com.pratham.foundation.modalclasses.Diagnostic_pdf_Modal;

import java.util.List;

public interface ShowTestPDFContract {

    interface ItemClicked {
        void openPDF(Diagnostic_pdf_Modal contentItem);
    }

    interface TestPDFView {
        void setList(List<Diagnostic_pdf_Modal> pdf_modalList);

        void setAdapter();

        void showNoData();
    }

    interface TestPDFPresenter {
        void setView(ShowTestPDFContract.TestPDFView testPDFView);

        void getPDFs();
    }

}