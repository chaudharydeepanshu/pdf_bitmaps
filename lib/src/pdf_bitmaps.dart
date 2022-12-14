import 'package:pdf_bitmaps/src/pdf_bitmaps_method_channel.dart';
import 'package:pdf_bitmaps/src/pdf_bitmaps_platform_interface.dart';

class PdfBitmaps {
  /// Get the page count for a pdf file.
  ///
  /// Returns the page count of a pdf file or null if operation was cancelled.
  /// Throws exception on error.
  Future<int?> pdfPageCount({PDFPageCountParams? params}) async {
    return PdfBitmapsPlatform.instance.pdfPageCount(params: params);
  }

  /// Get the specific page bitmap for a pdf file.
  ///
  /// Returns the bitmap or null if operation was cancelled.
  /// Throws exception on error.
  Future<String?> pdfBitmap({PDFBitmapParams? params}) async {
    return PdfBitmapsPlatform.instance.pdfBitmap(params: params);
  }

  /// Get the specific page bitmap for a pdf file.
  ///
  /// Returns the bitmap or null if operation was cancelled.
  /// Throws exception on error.
  Future<List<String>?> pdfBitmaps({PDFBitmapsParams? params}) async {
    return PdfBitmapsPlatform.instance.pdfBitmaps(params: params);
  }

  /// Get the specific page size info for a pdf file.
  ///
  /// Returns the bitmap or null if operation was cancelled.
  /// Throws exception on error.
  Future<PageSizeInfo?> pdfPageSize({PDFPageSizeParams? params}) async {
    return PdfBitmapsPlatform.instance.pdfPageSize(params: params);
  }

  /// Provides pdf file validity and protection info.
  ///
  /// Returns PdfValidityAndProtection for pdf file or null if operation was cancelled.
  /// Throws exception on error.
  Future<PdfValidityAndProtection?> pdfValidityAndProtection(
      {PDFValidityAndProtectionParams? params}) {
    return PdfBitmapsPlatform.instance.pdfValidityAndProtection(params: params);
  }

  /// Cancels running bitmaps operation.
  ///
  /// Returns the cancelling message.
  Future<String?> cancelBitmaps() {
    return PdfBitmapsPlatform.instance.cancelBitmaps();
  }
}
