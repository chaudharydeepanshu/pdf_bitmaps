import 'dart:typed_data';

import 'package:pdf_bitmaps/pdf_bitmaps.dart';
import 'package:pdf_bitmaps/src/pdf_bitmaps_platform_interface.dart';

class PdfBitmaps {
  /// Get the page count for a pdf file.
  ///
  /// Returns the page count of a pdf file or null if operation was cancelled.
  /// Throws exception on error.
  Future<int?> pdfPageCount({required String pdfUri}) async {
    return PdfBitmapsPlatform.instance.pdfPageCount(pdfUri: pdfUri);
  }

  /// Get the specific page bitmap for a pdf file.
  ///
  /// Returns the bitmap or null if operation was cancelled.
  /// Throws exception on error.
  Future<Uint8List?> pdfBitmap({PDFBitmapParams? params}) async {
    return PdfBitmapsPlatform.instance.pdfBitmap(params: params);
  }
}
